Aim
---

-   serve mobile clients with an address book to find contacts on
    buddycloud
-   quickly hook up new users with existing contacts
-   present them with a list "these are channels you might be interested
    in"

Acceessing the Service
----------------------

This is an open service for any client and users on any domain to query.

The service runs at **friend-finder.buddycloud.com**

User Flow
---------

1.  mobile clients contain identifiers from different sources (email,
    phone, twitter, skype etc)
2.  The client can, with the user's permission, read their address book
3.  client sends a SHA256 hash of the contact mime type and the contact
    identifier to the server. For example to send buddycloud's twitter
    handle, SHA256(vnd.twitter.profile<space>buddycloud)
4.  the server matches the hashes with existing hashes and sends the
    client
    1.  a list of jids to the client together with,
    2.  the matching hash

5.  the client displays looks up the matching hash from the address book
6.  the client displays the avatar shown the jid with a "follow" button

Creating Hashes
---------------
```
 sha256(email:name@domain.com)
 **sha256**(phone:<last 6 digits of phone number with spaces removed>)
 **sha256**(**lowercase**(vnd.android.cursor.item/vnd.fm.last.android.profile<space>**remove-white-space**(music-lover)))
```

Database Schema
---------------

~~~~ {.sql}
CREATE TABLE "contact-matches" (
    "jid" character varying(256) NOT NULL,
    "credential-hash" character varying(64) NOT NULL
-- 64characters x 8bit = 256
);



CREATE INDEX "jid-column-index" ON "contact-matches" USING btree (jid);
CREATE INDEX "credential-hash-column-index" ON "contact-matches" USING btree ("credential-hash");

COMMENT ON COLUMN "contact-matches".jid IS 'The sending JID';
COMMENT ON COLUMN "contact-matches"."credential-hash" IS 'https://buddycloud.org/wiki/Contact_matching#making_hashes';
~~~~

XMPP Stanzas
------------

The client sends

~~~~ {.xml}
<iq to="friendfinder.buddycloud.com" from="james@giantpeach.com" type="get" id="qadfqadfa">
            <!-- we need something in here to say "this is me, these are my hashes" -->
            <item item-hash="da39a3ee5e6b4b0d3255bfef95601890afd80709" me="true">
            <item item-hash="d6c26418ce0059c9bd46f4884864ceda6eafd0d2" me="true">
            <item item-hash="0164244061e12f1b374e0133b72bd7d0f3930d58" me="true">
            <item item-hash="a9fc9df17f991549d4d0928d67923c97d230007b">
            <item item-hash="0edfd414c0ea0c7e8ff93433673ddf810e00210b">
            <item item-hash="46a9773f2e10559d6656967c2e093da01a937480">
            <item item-hash="46a9773f2e10559d6656967c2e093da01a937480">
            <item item-hash="0edfd414c0ea0c7e8ff93433673ddf810e00210b">
            <item item-hash="6b0b106cfd2beab2e846606e21cb353a3a83c4c9">
</iq>
~~~~

Server returns a list of possible channels

~~~~ {.xml}
<iq to="james@giantpeach.com" from="friendfinder.buddycloud.com" type="set" id="qadfqadfa">
            <item jid="friend@buddycloud.com" matched-hash="0164244061e12f1b374e0133b72bd7d0f3930d58">
            <item jid="probably-a-friend@buddycloud.com" matched-hash="0164244061e12f1b374e0133b72bd7d0f3930d58">
            <item jid="another-probable-friend@example.com" matched-hash="0164244061e12f1b374e0133b72bd7d0f3930d58" >
 </iq>
~~~~

NOTE: returning the matched hash enables looking up the contact details
on the client and displaying the icon in the returned list of channels
to follow

Finding the Account Identifier on Android
-----------------------------------------

On Android, to find the mime types for each contact type, use the
following SQL (on a rooted phone). Other mobile platforms should use the
same mimetypes.

~~~~ {.sql}
sqlite> select * from mimetypes;
1|vnd.android.cursor.item/email_v2
2|vnd.android.cursor.item/im
3|vnd.android.cursor.item/nickname
4|vnd.android.cursor.item/organization
5|vnd.android.cursor.item/phone_v2
6|vnd.android.cursor.item/sip_address
7|vnd.android.cursor.item/name
8|vnd.android.cursor.item/postal-address_v2
9|vnd.android.cursor.item/identity
10|vnd.android.cursor.item/photo
11|vnd.android.cursor.item/group_membership
12|vnd.android.cursor.item/note
13|vnd.com.google.cursor.item/contact_misc
14|vnd.android.cursor.item/website
15|vnd.android.cursor.item/vnd.facebook.profile
16|vnd.android.cursor.item/vnd.googleplus.profile
17|vnd.android.cursor.item/vnd.twitter.profile
18|vnd.android.cursor.item/vnd.fm.last.android.profile
19|vnd.android.cursor.item/com.skype.android.chat.action
20|vnd.android.cursor.item/com.skype.android.skypecall.action
21|vnd.android.cursor.item/com.skype.android.videocall.action 

-- and now find the profile credentials

sqlite> select * from data where mimetype_id = 15;
2810||15|487|0|0|0|2|100000004412355|Facebook-Profil|Profil anzeigen||||||||||||||||
~~~~

Reference Material
------------------

-   <http://www.quora.com/User-Acquisition/What-is-the-best-invite-a-friend-flow-for-mobile>
-   <http://mattgemmell.com/2012/02/11/hashing-for-privacy-in-social-apps/>
-   <http://www.h-online.com/security/news/item/Path-iOS-app-now-hashes-address-book-data-1511858.html>
