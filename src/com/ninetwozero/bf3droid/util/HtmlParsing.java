package com.ninetwozero.bf3droid.util;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.datatype.LoginResult;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.SimplePlatoon;
import com.ninetwozero.bf3droid.datatype.UserInfo;
import com.ninetwozero.bf3droid.provider.table.UserProfileData;
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
    private final String PLATOON_XBOX = "common-game-2-2";
    private final String PLATOON_PS3 = "common-game-2-4";
    private final String PLATOON_PC = "common-game-2-1";
    private final String EMPTY_STRING = "";


    public LoginResult extractUserDetails(String httpContent) {
        document = Jsoup.parse(httpContent);
        if (loggedInPage()) {
            return extractUserCredentials();
        } else {
            return extractError(httpContent);
        }
    }

    public UserInfo extractUserInfo(String httpContent){
        document = Jsoup.parse(httpContent);
        List<SimplePersona> personas = extractUserPersonas();
        List<SimplePlatoon> platoons = extractPlatoons();
        UserProfileData profileData = extractUserProfile();
        return new UserInfo(personas, platoons, profileData);
    }

    protected List<SimplePersona> extractUserPersonas() {
        List<SimplePersona> personas = new ArrayList<SimplePersona>();
        Elements soldierList = document.select("#soldier-list li");
        for (Element element : soldierList) {
            personas.add(extractSimplePersona(element));
        }
        return personas;
    }

    protected List<SimplePlatoon> extractPlatoons() {
        List<SimplePlatoon> platoons = new ArrayList<SimplePlatoon>();
        Elements platoonElement = document.select(".profile-platoons li");
        for (Element element : platoonElement) {
            platoons.add(extractSimplePlatoon(element));
        }
        return platoons;
    }

    protected UserProfileData extractUserProfile() {
        String name = userProfileName();
        String age = userAge();
        String enlisted = userEnlisted();
        String lastSeen = userLastSeen();
        String presentation = userPresentation();
        String country = userCountry();
        int veteranStatus = userVeteranStatus();
        String statusMessage = userStatusMessage();
        return new UserProfileData(BF3Droid.getUserId(), BF3Droid.getUser(), name, age, enlisted, lastSeen,
                presentation, country, veteranStatus, statusMessage);
    }

    private boolean loggedInPage() {
        return userElement() != null;
    }

    private LoginResult extractUserCredentials() {
        Element userElement = userElement();
        String userName = userElement.text();
        long userId = userIdFromDocument();
        String checkSum = checkSumFromDocument();
        return new LoginResult(userName, userId, checkSum);
    }

    private Element userElement() {
        return document.select(".base-header-soldier-link").first();
    }

    private long userIdFromDocument() {
        Element id = document.select(".base-avatar-size-medium").first();
        return Long.parseLong(id.attr("rel"));
    }

    private String checkSumFromDocument() {
        Element input = document.select("input[name=post-check-sum]").first();
        return input.attr("value");
    }

    private SimplePersona extractSimplePersona(Element element) {
        long id = Long.parseLong(element.attr("data-id"));
        Element nameElement = element.select(".soldier-name h3 a").first();
        String name = nameElement.text();
        String platform = extractPlatform(nameElement.attr("href"));
        String soldierImage = extractSoldierImage(element.select("a img").first());
        return new SimplePersona(name, id, platform, soldierImage);
    }

    private String extractPlatform(String url) {
        String[] linkElements = url.split("/");
        int lastIndex = linkElements.length - 1;
        if (linkElements[lastIndex].equalsIgnoreCase(XBOX)) {
            return XBOX;
        } else if (linkElements[lastIndex].equalsIgnoreCase(PS3)) {
            return PS3;
        } else {
            return PC;
        }
    }

    private String extractSoldierImage(Element element) {
        String[] linkElements = element.attr("src").split("/");
        String lastElement = linkElements[linkElements.length - 1];
        return lastElement.substring(0, lastElement.indexOf('.'));
    }

    private SimplePlatoon extractSimplePlatoon(Element element) {
        String name = element.select(".profile-platoon-name").first().text();
        long id = idFromHref(element.select(".profile-platoon-name").first().attr("href"));
        String badge = element.select(".platoon-badge-item").attr("src");
        String platform = platoonPlatform(element);
        return new SimplePlatoon(name, id, badge, platform);
    }

    private String platoonPlatform(Element element) {
        String classAttributes = element.select(".profile-platoon-info span").first().attr("class");
        if (classAttributes.contains(PLATOON_XBOX)) {
            return XBOX;
        } else if (classAttributes.contains(PLATOON_PS3)) {
            return PS3;
        } else if (classAttributes.contains(PLATOON_PC)) {
            return PC;
        } else {
            return "";
        }
    }

    private long idFromHref(String href) {
        String[] linkElements = href.split("/");
        return Long.parseLong(linkElements[linkElements.length - 1]);
    }

    private String userProfileName(){
        Elements elements = document.select("#profile-information h2 img");
        if(elements.size() > 0 && elements.get(0).hasText()){
            return elements.get(0).ownText();
        }else{
            return EMPTY_STRING;
        }
    }

    private String userAge() {
        Elements li = profileInformationSecondListElements();
        return li.isEmpty() ? EMPTY_STRING : timeValueFor("Age", li);
    }

    private String userEnlisted() {
        Elements li = profileInformationSecondListElements();
        return li.isEmpty() ? EMPTY_STRING : timeValueFor("Enlisted", li);
    }

    private String userLastSeen() {
        Elements li = profileInformationSecondListElements();
        if (li.isEmpty()) {
            return EMPTY_STRING;
        } else {
            String result = timeValueFor("Last seen", li);
            return result.equals(EMPTY_STRING) ? "Online" : result;
        }
    }

    private String userPresentation() {
        Elements presentation = document.select("#profile-presentation");
        return presentation.size() > 0 ? presentation.first().ownText() : EMPTY_STRING;
    }

    private String userCountry() {
        Elements elements = document.select("#profile-information h2 img");
        return elements.size() > 0 ? elements.first().attr("alt") : EMPTY_STRING;
    }

    private int userVeteranStatus() {
        Elements elements = document.select("#profile-veteran-status .profile-veteran-status");
        return elements.size() > 0 ? Integer.parseInt(elements.first().text()) : 0;
    }

    private String userStatusMessage(){
        Elements elements = document.select(".profile-status-message-text");
        return elements.size() > 0 ? elements.first().ownText() : EMPTY_STRING;
    }

    private Elements profileInformationSecondListElements() {
        Elements ul = document.select("#profile-information ul");
        return ul.size() > 1 ? ul.get(1).select("li") : new Elements();
    }

    private String timeValueFor(String key, Elements elements) {
        for (Element element : elements) {
            if (element.ownText().contains(key)) {
                return element.select("time").first().text().trim();
            }
        }
        return EMPTY_STRING;
    }

    private LoginResult extractError(String httpContent) {
        return new LoginResult(elementUidLinkError(httpContent));
    }

    //TODO refactor to use Jsoup
    private String elementUidLinkError(String httpContent) {
        int startPosition = httpContent.indexOf(ELEMENT_ERROR_MESSAGE);

        if (startPosition == -1) {
            return "The website won't let us in. Please try again later.";
        } else {
            int endPosition = httpContent.indexOf("</div>", startPosition);
            return httpContent.substring(startPosition, endPosition)
                    .replace("</div>", "")
                    .replace("\n", "")
                    .replace(ELEMENT_ERROR_MESSAGE, "");
        }
    }
}
