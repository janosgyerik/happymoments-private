CREATE TABLE "happymoments_happymoment" (
    "_id" integer NULL PRIMARY KEY AUTOINCREMENT,
    "text" text NULL,
    "filename" varchar(80) NULL,
    "color" varchar(80) NULL,
    "level" integer NULL,
    "latitude" real NULL,
    "longitude" real NULL,
    "speed" real NULL,
    "is_active" bool NULL,
    "created_dt" datetime NULL,
    "updated_dt" datetime NULL
)
;
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
