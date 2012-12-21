package com.ninetwozero.bf3droid.util;

import com.ninetwozero.bf3droid.Battlelog;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.WebsiteHandlerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

//TODO need some safety check if user details, personas and platoons found like personas must be at least one, and it does found platoons section
public class HtmlParsing {

    public static final String ELEMENT_ERROR_MESSAGE = "<div class=\"gate-login-errormsg wfont\">";
    public static final String ELEMENT_UID_LINK = "<a class=\"base-header-soldier-link\" href=\"/bf3/soldier/";
    public static final String ELEMENT_STATUS_CHECKSUM = "<input type=\"hidden\" name=\"post-check-sum\" value=\"";
    public static final String ELEMENT_USERNAME_LINK = "<div class=\"base-header-profile-username\">";

    private Document document;
    private final String XBOX = "Xbox";
    private final String PS3 = "PS3";
    private final String PC = "PC";


    public String extractUserDetails(String httpContent){
        String message = "";
        document = Jsoup.parse(httpContent);
        if(loggedInPage()){
            extractUserCredentials();
        } else {
            extractError();
        }

        return message;
    }

    public String extractUserData(String httpContent){
        document = Jsoup.parse(httpContent);
        extractUserPersonas();
        extractPlatoons();
        return "";
    }

    private boolean loggedInPage(){
        return userElement() != null;
    }

    private void extractUserCredentials() {
        Element userElement = userElement();
        Battlelog.setUser(userElement.text());
        Battlelog.setUserId(userIdFromDocument());
        Battlelog.setCheckSum(checkSumFromDocument());
    }

    private Element userElement() {
        return document.select(".base-header-soldier-link").first();
    }

    private long userIdFromDocument(){
        Element id = document.select(".base-avatar-size-medium").first();
        return Long.parseLong(id.attr("rel"));
    }

    private String checkSumFromDocument(){
        Element input = document.select("input[name=post-check-sum]").first();
        return input.attr("value");
    }

    private void extractUserPersonas() {
        List<SimplePersona> personas = new ArrayList<SimplePersona>();
        Elements soldierList = document.select("#soldier-list li");
        for(Element element : soldierList){
            personas.add(extractSimplePersona(element));
        }
        Battlelog.setUserPersonas(personas);
    }

    private SimplePersona extractSimplePersona(Element element){
        long id = Long.parseLong(element.attr("data-id"));
        Element nameElement = element.select(".soldier-name h3 a").first();
        String name = nameElement.text();
        String platform = extractPlatform(nameElement.attr("href"));
        String soldierImage = extractSoldierImage(element.select("a img").first());
        return new SimplePersona(name, id, platform, soldierImage);
    }

    private String extractPlatform(String url){
        String[] linkElements = url.split("/");
        int lastIndex = linkElements.length - 1;
        if(linkElements[lastIndex].equalsIgnoreCase(XBOX)){
            return XBOX;
        } else if(linkElements[lastIndex].equals(PS3)){
            return PS3;
        } else {
            return PC;
        }
    }

    private String extractSoldierImage(Element element){
        String[] linkElements = element.attr("src").split("/");
        String lastElement = linkElements[linkElements.length -1];
        return lastElement.substring(0, lastElement.indexOf('.'));
    }

    private void extractPlatoons() {
        List<SimplePlatoon> platoons = new ArrayList<SimplePlatoon>();
        Elements platoonElement = document.select(".profile-platoons li");
        for(Element element : platoonElement){
            platoons.add(extractSimplePlatoon(element));
        }
        Battlelog.setUserPlatoons(platoons);
    }

    private SimplePlatoon extractSimplePlatoon(Element element) {
        String name = element.select(".profile-platoon-name").first().text();
        long id = idFromHref(element.select(".profile-platoon-name").first().attr("href"));
        String badge = element.select(".platoon-badge-item").attr("src");
        return new SimplePlatoon(name, id, badge);
    }

    private long idFromHref(String href){
        String[] linkElements = href.split("/");
        return Long.parseLong(linkElements[linkElements.length - 1]);
    }

    private void extractError(){

    }

    private void elementUidLinkError(String httpContent) throws WebsiteHandlerException {
        int startPosition = httpContent.indexOf(ELEMENT_ERROR_MESSAGE);

        if (startPosition == -1) {
            throw new WebsiteHandlerException(
                    "The website won't let us in. Please try again later.");
        } else {
            int endPosition = httpContent.indexOf("</div>", startPosition);
            String errorMsg = httpContent.substring(startPosition, endPosition)
                    .replace("</div>", "")
                    .replace("\n", "")
                    .replace(ELEMENT_ERROR_MESSAGE, "");
            throw new WebsiteHandlerException(errorMsg);
        }
    }
}
