INSERT INTO "auth"."roles"
    ("role", "description")
VALUES ('USER', 'Common user role'),
       ('DEVELOPER', 'Developer role'),
       ('ADMIN', 'Administrator role')
ON CONFLICT
    ("role")
    DO NOTHING;

-- Test Data

-- 1. Users (password is 'password')
INSERT INTO "auth"."users" (id, username, email, password, email_verified)
    VALUES (1, 'user', 'user@example.com', '$2a$10$iqETs16B3bMjyNrzUlJ7FO2NTIYMU/BGTzHsV2UMwIoJuue3BM9g2', true),
       (2, 'developer', 'dev@example.com', '$2a$10$iqETs16B3bMjyNrzUlJ7FO2NTIYMU/BGTzHsV2UMwIoJuue3BM9g2', true)
ON CONFLICT (id) DO NOTHING;

-- 2. User Roles
INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 1, id FROM "auth"."roles" WHERE role = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 2, id FROM "auth"."roles" WHERE role = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 2, id FROM "auth"."roles" WHERE role = 'DEVELOPER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 3. Developer Details
INSERT INTO "catalog"."developers" (id, name, description)
VALUES (2, 'Epic Devs', 'Creators of awesome games')
ON CONFLICT (id) DO NOTHING;

-- 4. Games
INSERT INTO "catalog"."games" (id, developer_id, title, description, price, image_url)
VALUES (1, 2, 'Cyberpunk 2077', 'A futuristic RPG', 59.99,
        'https://enderg.com/wp-content/uploads/2022/08/cyberpunk-2077-box-art-01-ps4-us-06jun19.jpg'),
       (2, 2, 'The Witcher 3', 'A fantasy RPG', 39.99,
        'https://hips.hearstapps.com/hmg-prod/images/the-witcher-temporada-3-henry-cavill-6490748eea887.jpg')
ON CONFLICT (id) DO NOTHING;

-- 5. Game Keys
INSERT INTO "checkout"."game_keys" (game_id, key_value)
VALUES (1, 'CP77-KEY-1'), (1, 'CP77-KEY-2'), (1, 'CP77-KEY-3'), (1, 'CP77-KEY-4'),
       (2, 'TW3-KEY-1'), (2, 'TW3-KEY-2'), (2, 'TW3-KEY-3'), (2, 'TW3-KEY-4')
ON CONFLICT (key_value) DO NOTHING;

-- 6. Sync Sequences
SELECT setval('auth.users_id_seq', (SELECT MAX(id) FROM auth.users));
SELECT setval('catalog.games_id_seq', (SELECT MAX(id) FROM catalog.games));
SELECT setval('checkout.game_keys_id_seq', (SELECT MAX(id) FROM checkout.game_keys));



