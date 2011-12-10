Strings to be translated below this information block
ONLY items that are not part of a tag, ie between >HERE</

#######################################################################
# Example (these should not be translated):
#######################################################################
	
<string name="changelog">
</string>
<string-array name="service_update_intervals_labels">
</string-array>


########################################################################
# However, the values between the tags should be translated, like below:
########################################################################

<string name="changelog">This text here should be translated</string>
<item>THIS SHOULD ALSO BE TRANSLATED</item>
&lt;br /&gt; should remain intact (new line)

########################################################################
# ALSO, values wrapped in {} are not to be translated, such as below:
########################################################################

{date}
{username}

Obviously you might have to move them around in the sentence, which is
fully acceptable as the translations need to be accurate. I have faith
in you people. :-)

########################################################################
# Last, but not least
########################################################################

Thank you for your help - your effort is much appreciated by both me and 
the user base. Currently there's somewhere between 30 000- 50 000 users 
PER day, and somewhat over 50 000 downloads all in all.

Best regards,
Karl Lindmark 