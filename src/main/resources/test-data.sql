-- ==========================================
-- GameVault Seed Data (Test Values)
-- ==========================================

BEGIN;

-- ------------------------------------------------------
-- 1. AUTH SCHEMA
-- ------------------------------------------------------

-- Insert Roles
INSERT INTO "auth"."roles" ("id", "role", "description")
VALUES (1, 'USER', 'Standard registered user who can purchase games'),
       (2, 'DEVELOPER', 'User who can upload and manage their own games'),
       (3, 'ADMIN', 'Platform administrator with moderation privileges');

-- Insert Users (Passwords are hashed placeholders: 'password123')
INSERT INTO "auth"."users" ("id", "username", "email", "password", "bio", "email_verified", "created_at")
VALUES (1, 'admin_user', 'admin@gamevault.com', '$2a$12$GwF/b/d/a/hashedplaceholder', 'System Administrator', true,
        NOW()),
       (2, 'dev_studio', 'contact@indiedev.com', '$2a$12$GwF/b/d/a/hashedplaceholder', 'We make retro pixel art games.',
        true, NOW()),
       (3, 'player_one', 'player@gmail.com', '$2a$12$GwF/b/d/a/hashedplaceholder', 'Casual gamer.', true, NOW());

-- Assign Roles
INSERT INTO "auth"."user_roles" ("user_id", "role_id")
VALUES (1, 3),
       (2, 2),
       (3, 1);

-- ------------------------------------------------------
-- 2. CATALOG SCHEMA
-- ------------------------------------------------------

-- Insert Game Tags
INSERT INTO "catalog"."game_tags" ("id", "name")
VALUES (1, 'Action'),
       (2, 'RPG'),
       (3, 'Indie'),
       (4, 'Strategy'),
       (5, 'Simulation');

-- Insert Games
-- Note: 'dev_studio' (id: 2) is the developer
INSERT INTO "catalog"."games" ("id", "developer_id", "title", "description", "price", "release_data", "status",
                               "created_at")
VALUES (1, 2, 'Neon Cyber-Slayer', 'A fast-paced cyberpunk slasher with neon aesthetics.', 19.99, '2023-11-15',
        'APPROVED', NOW()),
       (2, 2, 'Medieval Tycoon', 'Build your own castle and manage the economy.', 29.99, '2024-01-10', 'APPROVED',
        NOW()),
       (3, 2, 'Glitchy Alpha', 'An early access build of our next big hit.', 0.00, '2025-01-01', 'PENDING_APPROVAL',
        NOW());

-- Link Games to Tags
INSERT INTO "catalog"."game_game_tags" ("game_id", "tag_id")
VALUES (1, 1),
       (1, 3), -- Neon Cyber-Slayer is Action, Indie
       (2, 4),
       (2, 5), -- Medieval Tycoon is Strategy, Simulation
       (3, 3);
-- Glitchy Alpha is Indie

-- ------------------------------------------------------
-- 3. CART SCHEMA
-- ------------------------------------------------------

-- Initialize Shopping Carts (One per user is standard practice)
INSERT INTO "cart"."shopping_carts" ("user_id")
VALUES (1),
       (2),
       (3);

-- Add items to Player One's cart (User 3)
INSERT INTO "cart"."cart_items" ("id", "user_id", "game_id", "price_at_addition")
VALUES (1, 3, 2, 29.99);
-- Player has 'Medieval Tycoon' in cart

-- Add items to Wishlist
INSERT INTO "cart"."wishlist" ("user_id", "game_id")
VALUES (3, 1);
-- Player wants 'Neon Cyber-Slayer'

-- ------------------------------------------------------
-- 4. CHECKOUT SCHEMA
-- ------------------------------------------------------

-- Create a past Order for Player One (User 3)
INSERT INTO "checkout"."orders" ("id", "user_id", "total_price", "status", "stripe_session_id", "created_at")
VALUES (1, 3, 19.99, 'PAID', 'cs_test_a1b2c3d4e5f6g7h8i9j0', NOW());

-- Create Order Item for that Order
INSERT INTO "checkout"."order_items" ("id", "order_id", "game_id", "purchased_price")
VALUES (1, 1, 1, 19.99);
-- Bought 'Neon Cyber-Slayer'

-- Generate a Game Key for the purchased item
INSERT INTO "checkout"."game_keys" ("id", "game_id", "key_value", "used_at", "order_item_id")
VALUES (1, 1, 'XXXX-YYYY-ZZZZ-NEON', NOW(), 1);

-- Record that the user now owns the game
INSERT INTO "checkout"."user_purchased_games" ("user_id", "game_id")
VALUES (3, 1);

-- ------------------------------------------------------
-- 5. REFRESH & TOKENS (Optional for seed, good for integration tests)
-- ------------------------------------------------------

INSERT INTO "auth"."refresh_tokens" ("user_id", "token", "created_at")
VALUES (3, 'refresh_token_sample_12345', NOW());

COMMIT;