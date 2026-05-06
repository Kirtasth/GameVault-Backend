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
       (2, 'developer', 'dev@example.com', '$2a$10$iqETs16B3bMjyNrzUlJ7FO2NTIYMU/BGTzHsV2UMwIoJuue3BM9g2', true),
       (3, 'nebula_dev', 'nebula@example.com', '$2a$10$iqETs16B3bMjyNrzUlJ7FO2NTIYMU/BGTzHsV2UMwIoJuue3BM9g2', true),
       (4, 'iron_forge', 'ironforge@example.com', '$2a$10$iqETs16B3bMjyNrzUlJ7FO2NTIYMU/BGTzHsV2UMwIoJuue3BM9g2', true),
       (5, 'pixel_pulse', 'pixelpulse@example.com', '$2a$10$iqETs16B3bMjyNrzUlJ7FO2NTIYMU/BGTzHsV2UMwIoJuue3BM9g2', true)
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

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 3, id FROM "auth"."roles" WHERE role = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 3, id FROM "auth"."roles" WHERE role = 'DEVELOPER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 4, id FROM "auth"."roles" WHERE role = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 4, id FROM "auth"."roles" WHERE role = 'DEVELOPER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 5, id FROM "auth"."roles" WHERE role = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO "auth"."user_roles" (user_id, role_id)
SELECT 5, id FROM "auth"."roles" WHERE role = 'DEVELOPER'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- 3. Developer Details
INSERT INTO "catalog"."developers" (id, name, description)
VALUES (2, 'Epic Devs', 'Creators of awesome games'),
       (3, 'Nebula Interactive', 'Specialists in high-fidelity sci-fi and space exploration experiences.'),
       (4, 'Iron Forge Studios', 'Dedicated to crafting deep, atmospheric fantasy and medieval combat games.'),
       (5, 'Pixel Pulse Games', 'Pushing the boundaries of the cyberpunk aesthetic and retro-inspired gameplay.')
ON CONFLICT (id) DO NOTHING;

-- 4. Games
INSERT INTO "catalog"."games" (id, developer_id, title, description, price, image_url)
VALUES (1, 2, 'Cyberpunk 2077', 'A futuristic RPG', 59.99,
        'https://enderg.com/wp-content/uploads/2022/08/cyberpunk-2077-box-art-01-ps4-us-06jun19.jpg'),
       (2, 2, 'The Witcher 3', 'A fantasy RPG', 39.99,
        'https://hips.hearstapps.com/hmg-prod/images/the-witcher-temporada-3-henry-cavill-6490748eea887.jpg'),
       -- Nebula Interactive (ID 3)
       (3, 3, 'Starbound Frontiers', 'An open-world space exploration RPG where players pilot customizable ships across procedurally generated galaxies. Discover ancient alien civilizations and trade rare resources in a living universe.', 49.99, 'https://images.unsplash.com/photo-1444703686981-a3abbc4d4fe3?auto=format&fit=crop&q=80&w=800'),
       (4, 3, 'Void Runner', 'A high-speed arcade racer set in the vacuum of space, featuring gravity-defying tracks and pulse-pounding electronic music. Experience the ultimate rush in zero-G.', 19.99, 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=800'),
       (5, 3, 'Galactic Tycoon', 'Manage your own orbital station, balancing resource production, tourist satisfaction, and defense against space pirates in this deep and rewarding simulation.', 29.99, 'https://images.unsplash.com/photo-1541185933-ef5d8ed016c2?auto=format&fit=crop&q=80&w=800'),
       (6, 3, 'Nebula Rift', 'A tactical turn-based strategy game where you lead a fleet of capital ships through a volatile rift in space-time. Command your forces with precision and cunning.', 34.99, 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&q=80&w=800'),
       (7, 3, 'Solar Siege', 'A base-defense game where you protect a massive Dyson sphere from relentless waves of mechanical invaders. Upgrade your defenses and survive the onslaught.', 24.99, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&q=80&w=800'),
       -- Iron Forge Studios (ID 4)
       (8, 4, 'Hammer & Anvil', 'A deep blacksmithing simulator where you craft legendary weapons for adventurers, managing your shop''s reputation and resources in a bustling fantasy city.', 14.99, 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&q=80&w=800'),
       (9, 4, 'Siege of Valhalla', 'A massive-scale battlefield game featuring historical Norse combat, castle sieges, and mythic beasts. Lead your clan to eternal glory in the halls of Valhalla.', 39.99, 'https://images.unsplash.com/photo-1533106497176-45ae19e68ba2?auto=format&fit=crop&q=80&w=800'),
       (10, 4, 'Runemaster', 'A puzzle-adventure game where you manipulate ancient ruins using a complex system of magical runes. Uncover the secrets of a forgotten civilization.', 24.99, 'https://images.unsplash.com/photo-1514539079130-25950c84af65?auto=format&fit=crop&q=80&w=800'),
       (11, 4, 'Iron Legions', 'A real-time strategy game focusing on squad-based tactics and industrial-age warfare in a fantasy setting. Command powerful legions and conquer the realm.', 29.99, 'https://images.unsplash.com/photo-1516192511155-2c14f063ecf8?auto=format&fit=crop&q=80&w=800'),
       (12, 4, 'Mythic Blade', 'A challenging third-person action game featuring precise swordplay and punishing boss encounters in a ruined kingdom. Master the art of the blade and survive.', 34.99, 'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&q=80&w=800'),
       -- Pixel Pulse Games (ID 5)
       (13, 5, 'Neon Drifter', 'A narrative-driven cyberpunk detective game set in a rainy, neon-soaked metropolis where every choice affects the city''s delicate power balance. Solve the ultimate conspiracy.', 29.99, 'https://images.unsplash.com/photo-1605810230434-7631ac76ec81?auto=format&fit=crop&q=80&w=800'),
       (14, 5, 'Synthwave City', 'A vibrant city-builder where the layout of your streets and buildings generates a unique synthwave soundtrack. Build the ultimate neon utopia.', 19.99, 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=800'),
       (15, 5, 'Bit-Shift', 'A mind-bending platformer where you must toggle between 8-bit and 32-bit versions of the level to overcome obstacles. A nostalgic journey through gaming history.', 14.99, 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=800'),
       (16, 5, 'Glitch Protocol', 'A stealth-hacking game where you infiltrate high-security corporate networks by exploiting digital anomalies. Become the ultimate ghost in the machine.', 24.99, 'https://images.unsplash.com/photo-1563986768609-322da13575f3?auto=format&fit=crop&q=80&w=800'),
       (17, 5, 'Retro Racer', 'A love letter to 90s arcade racers, featuring chunky polygons, drifting mechanics, and a collection of classic-inspired cars. Feel the nostalgia of the fast lane.', 9.99, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&q=80&w=800')
ON CONFLICT (id) DO NOTHING;

-- 5. Game Keys
INSERT INTO "checkout"."game_keys" (game_id, key_value)
VALUES (1, 'CP77-KEY-1'), (1, 'CP77-KEY-2'), (1, 'CP77-KEY-3'), (1, 'CP77-KEY-4'),
       (2, 'TW3-KEY-1'), (2, 'TW3-KEY-2'), (2, 'TW3-KEY-3'), (2, 'TW3-KEY-4'),
       -- Starbound Frontiers (3)
       (3, 'SF-KEY-1'), (3, 'SF-KEY-2'), (3, 'SF-KEY-3'),
       -- Galactic Tycoon (5)
       (5, 'GT-KEY-1'), (5, 'GT-KEY-2'), (5, 'GT-KEY-3'),
       -- Nebula Rift (6)
       (6, 'NR-KEY-1'), (6, 'NR-KEY-2'), (6, 'NR-KEY-3'),
       -- Solar Siege (7)
       (7, 'SS-KEY-1'), (7, 'SS-KEY-2'), (7, 'SS-KEY-3'),
       -- Hammer & Anvil (8)
       (8, 'HA-KEY-1'), (8, 'HA-KEY-2'), (8, 'HA-KEY-3'),
       -- Siege of Valhalla (9)
       (9, 'SV-KEY-1'), (9, 'SV-KEY-2'), (9, 'SV-KEY-3'),
       -- Runemaster (10)
       (10, 'RM-KEY-1'), (10, 'RM-KEY-2'), (10, 'RM-KEY-3'),
       -- Iron Legions (11)
       (11, 'IL-KEY-1'), (11, 'IL-KEY-2'), (11, 'IL-KEY-3'),
       -- Neon Drifter (13)
       (13, 'ND-KEY-1'), (13, 'ND-KEY-2'), (13, 'ND-KEY-3'),
       -- Synthwave City (14)
       (14, 'SC-KEY-1'), (14, 'SC-KEY-2'), (14, 'SC-KEY-3'),
       -- Bit-Shift (15)
       (15, 'BS-KEY-1'), (15, 'BS-KEY-2'), (15, 'BS-KEY-3'),
       -- Retro Racer (17)
       (17, 'RR-KEY-1'), (17, 'RR-KEY-2'), (17, 'RR-KEY-3')
ON CONFLICT (key_value) DO NOTHING;

-- 6. Sync Sequences
SELECT setval('auth.users_id_seq', (SELECT MAX(id) FROM auth.users));
SELECT setval('catalog.games_id_seq', (SELECT MAX(id) FROM catalog.games));
SELECT setval('checkout.game_keys_id_seq', (SELECT MAX(id) FROM checkout.game_keys));
