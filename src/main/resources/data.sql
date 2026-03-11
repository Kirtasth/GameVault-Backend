INSERT INTO "auth"."roles"
    ("role", "description")
VALUES ('USER', 'Common user role'),
       ('DEVELOPER', 'Developer role'),
       ('ADMIN', 'Administrator role')
ON CONFLICT
    ("role")
    DO NOTHING;