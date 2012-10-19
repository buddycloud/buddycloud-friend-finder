CREATE TABLE "contact-matches" (
    "jid" CHARACTER VARYING(256) NOT NULL,
    "credential-hash" CHARACTER VARYING(64) NOT NULL
-- 64characters x 8bit = 256
);
 
CREATE INDEX "jid-column-index" ON "contact-matches" USING btree (jid);
CREATE INDEX "credential-hash-column-index" ON "contact-matches" USING btree ("credential-hash");
 
COMMENT ON COLUMN "contact-matches".jid IS 'The sending JID';
COMMENT ON COLUMN "contact-matches"."credential-hash" IS 'https://buddycloud.org/wiki/Contact_matching#making_hashes';