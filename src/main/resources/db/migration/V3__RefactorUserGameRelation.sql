/*
    Refactor Game Keys and User relation
*/

-- 1. Add new columns to game_keys
ALTER TABLE "checkout"."game_keys" ADD COLUMN "reserved_by_user_id" bigint;
ALTER TABLE "checkout"."game_keys" ADD COLUMN "reserved_deadline" timestamp;
ALTER TABLE "checkout"."game_keys" ADD COLUMN "purchased_by_user_id" bigint;

-- 2. Add foreign keys for the new columns
ALTER TABLE "checkout"."game_keys" 
    ADD CONSTRAINT "fk_reserved_by_user" FOREIGN KEY ("reserved_by_user_id") REFERENCES "auth"."users" ("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "checkout"."game_keys" 
    ADD CONSTRAINT "fk_purchased_by_user" FOREIGN KEY ("purchased_by_user_id") REFERENCES "auth"."users" ("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- 3. Modify order_items to link to game_keys
ALTER TABLE "checkout"."order_items" ADD COLUMN "game_key_id" bigint;

-- 4. Set game_key_id in order_items from existing data
-- We need to find the game_key that has the current order_item_id
UPDATE "checkout"."order_items" oi
SET "game_key_id" = gk."id"
FROM "checkout"."game_keys" gk
WHERE gk."order_item_id" = oi."id";

-- 5. Set purchased_by_user_id in game_keys from existing orders
UPDATE "checkout"."game_keys" gk
SET "purchased_by_user_id" = o."user_id"
FROM "checkout"."orders" o
JOIN "checkout"."order_items" oi ON o."id" = oi."order_id"
WHERE gk."order_item_id" = oi."id";

-- 6. Add foreign key for game_key_id in order_items
ALTER TABLE "checkout"."order_items" 
    ADD CONSTRAINT "fk_order_item_game_key" FOREIGN KEY ("game_key_id") REFERENCES "checkout"."game_keys" ("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- 7. Remove old column from game_keys
ALTER TABLE "checkout"."game_keys" DROP COLUMN IF EXISTS "order_item_id";

-- 8. Remove user_purchased_games table
DROP TABLE IF EXISTS "checkout"."user_purchased_games";
