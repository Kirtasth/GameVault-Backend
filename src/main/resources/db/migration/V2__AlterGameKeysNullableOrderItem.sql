ALTER TABLE "checkout"."game_keys" ALTER COLUMN "order_item_id" DROP NOT NULL;

ALTER TABLE "checkout"."game_keys" ADD COLUMN "created_at" TIMESTAMP NOT NULL DEFAULT (now());
ALTER TABLE "checkout"."game_keys" ADD COLUMN "updated_at" TIMESTAMP NOT NULL DEFAULT (now());
