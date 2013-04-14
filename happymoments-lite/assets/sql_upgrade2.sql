CREATE TABLE "happymoments_happymomentphoto" (
    "_id" integer NULL PRIMARY KEY AUTOINCREMENT,
    "happy_moment_id" integer NULL REFERENCES "happymoments_happymoment" ("_id"),
    "filename" varchar(50) NULL,
    "display_order" integer NULL,
    "created_dt" datetime NULL,
    "updated_dt" datetime NULL,
    UNIQUE ("happy_moment_id", "filename")
)
;
