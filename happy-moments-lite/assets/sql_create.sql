CREATE TABLE "happymoments_happymoment" (
    "_id" integer NULL PRIMARY KEY AUTOINCREMENT,
    "text" text NULL,
    "filename" varchar(80) NULL,
    "color" varchar(80) NULL,
    "level" integer NULL,
    "is_active" bool NULL,
    "created_dt" datetime NULL,
    "updated_dt" datetime NULL
)
;
