CREATE SCHEMA "auth";

CREATE SCHEMA "catalog";

CREATE SCHEMA "cart";

CREATE SCHEMA "checkout";

CREATE TYPE "user_role" AS ENUM (
  'USER',
  'DEVELOPER',
  'ADMIN'
);

CREATE TYPE "game_status" AS ENUM (
  'PENDING_APPROVAL',
  'APPROVED',
  'REJECTED'
);

CREATE TYPE "order_status" AS ENUM (
  'PENDING',
  'PAID',
  'FAILED',
  'CANCELED'
);

CREATE TABLE "auth"."users"
(
    "id"                  bigserial PRIMARY KEY,
    "username"            varchar(50) UNIQUE  NOT NULL,
    "email"               varchar(255) UNIQUE NOT NULL,
    "password"            varchar(255)        NOT NULL,
    "avatar_url"          varchar(512),
    "bio"                 text,
    "email_verified"      bool                NOT NULL DEFAULT false,
    "account_enabled"     bool                NOT NULL DEFAULT true,
    "account_expired"     bool                NOT NULL DEFAULT false,
    "account_locked"      bool                NOT NULL DEFAULT false,
    "credentials_expired" bool                NOT NULL DEFAULT false,
    "lock_reason"         text,
    "lock_instant"        timestamp,
    "created_at"          timestamp           NOT NULL DEFAULT (now()),
    "updated_at"          timestamp           NOT NULL DEFAULT (now()),
    "deleted_at"          timestamp
);

CREATE TABLE "auth"."roles"
(
    "id"          bigserial PRIMARY KEY,
    "role"        user_role UNIQUE NOT NULL,
    "description" text,
    "created_at"  timestamp NOT NULL DEFAULT (now()),
    "updated_at"  timestamp NOT NULL DEFAULT (now()),
    "deleted_at"  timestamp
);

CREATE TABLE "auth"."user_roles"
(
    "user_id"     bigint,
    "role_id"     bigint,
    "assigned_at" timestamp NOT NULL DEFAULT (now()),
    PRIMARY KEY ("user_id", "role_id")
);

CREATE TABLE "auth"."refresh_tokens"
(
    "id"         bigserial PRIMARY KEY,
    "user_id"    bigint              NOT NULL,
    "token"      varchar(512) UNIQUE NOT NULL,
    "created_at" timestamp           NOT NULL DEFAULT (now()),
    "revoked_at" timestamp
);

CREATE TABLE "auth"."password_reset_tokens"
(
    "id"         bigserial PRIMARY KEY,
    "user_id"    bigint              NOT NULL,
    "token"      varchar(512) UNIQUE NOT NULL,
    "created_at" timestamp           NOT NULL DEFAULT (now()),
    "used_at"    timestamp,
    "expires_at" timestamp           NOT NULL
);

CREATE TABLE "catalog"."games"
(
    "id"           bigserial PRIMARY KEY,
    "developer_id" bigint              NOT NULL,
    "title"        varchar(255) UNIQUE NOT NULL,
    "description"  text,
    "price"        numeric             NOT NULL DEFAULT 0,
    "release_data" date,
    "status"       game_status         NOT NULL DEFAULT 'PENDING_APPROVAL',
    "created_at"   timestamp           NOT NULL DEFAULT (now()),
    "updated_at"   timestamp           NOT NULL DEFAULT (now()),
    "deleted_at"   timestamp
);

CREATE TABLE "catalog"."game_tags"
(
    "id"   bigserial PRIMARY KEY,
    "name" varchar(255) UNIQUE NOT NULL
);

CREATE TABLE "catalog"."game_game_tags"
(
    "game_id" bigint NOT NULL,
    "tag_id"  bigint NOT NULL
);

CREATE TABLE "cart"."wishlist"
(
    "user_id"  bigint,
    "game_id"  bigint,
    "added_at" timestamp DEFAULT (now()),
    PRIMARY KEY ("user_id", "game_id")
);

CREATE TABLE "cart"."shopping_carts"
(
    "user_id"    bigint PRIMARY KEY,
    "created_at" timestamp NOT NULL DEFAULT (now()),
    "updated_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "cart"."cart_items"
(
    "id"                bigserial PRIMARY KEY,
    "user_id"           bigint  NOT NULL,
    "game_id"           bigint  NOT NULL,
    "price_at_addition" numeric NOT NULL
);

CREATE TABLE "checkout"."orders"
(
    "id"                bigserial PRIMARY KEY,
    "user_id"           bigint       NOT NULL,
    "total_price"       numeric      NOT NULL,
    "status"            order_status NOT NULL DEFAULT ('PENDING'),
    "stripe_session_id" varchar(255) UNIQUE,
    "created_at"        timestamp    NOT NULL DEFAULT (now()),
    "updated_at"        timestamp    NOT NULL DEFAULT (now())
);

CREATE TABLE "checkout"."order_items"
(
    "id"              bigserial PRIMARY KEY,
    "order_id"        bigint  NOT NULL,
    "game_id"         bigint  NOT NULL,
    "purchased_price" numeric NOT NULL
);

CREATE TABLE "checkout"."game_keys"
(
    "id"            bigserial PRIMARY KEY,
    "game_id"       bigint              NOT NULL,
    "key_value"     varchar(255) UNIQUE NOT NULL,
    "used_at"       timestamp,
    "order_item_id" bigint              NOT NULL
);

CREATE TABLE "checkout"."user_purchased_games"
(
    "user_id" bigint,
    "game_id" bigint,
    PRIMARY KEY ("user_id", "game_id")
);



ALTER TABLE "auth"."user_roles"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "auth"."user_roles"
    ADD FOREIGN KEY ("role_id") REFERENCES "auth"."roles" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "auth"."refresh_tokens"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "auth"."password_reset_tokens"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."games"
    ADD FOREIGN KEY ("developer_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."game_game_tags"
    ADD FOREIGN KEY ("game_id") REFERENCES "catalog"."games" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."game_game_tags"
    ADD FOREIGN KEY ("tag_id") REFERENCES "catalog"."game_tags" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "cart"."wishlist"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "cart"."wishlist"
    ADD FOREIGN KEY ("game_id") REFERENCES "catalog"."games" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "cart"."shopping_carts"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "cart"."shopping_carts"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "cart"."cart_items"
    ADD FOREIGN KEY ("game_id") REFERENCES "catalog"."games" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."orders"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."order_items"
    ADD FOREIGN KEY ("order_id") REFERENCES "checkout"."orders" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."order_items"
    ADD FOREIGN KEY ("game_id") REFERENCES "catalog"."games" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."game_keys"
    ADD FOREIGN KEY ("game_id") REFERENCES "catalog"."games" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."game_keys"
    ADD FOREIGN KEY ("order_item_id") REFERENCES "checkout"."order_items" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."user_purchased_games"
    ADD FOREIGN KEY ("user_id") REFERENCES "auth"."users" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "checkout"."user_purchased_games"
    ADD FOREIGN KEY ("game_id") REFERENCES "catalog"."games" ("id") ON DELETE CASCADE ON UPDATE CASCADE;