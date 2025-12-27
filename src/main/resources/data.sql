TRUNCATE TABLE
    -- Auth Context
    "auth"."users",
    "auth"."roles",
    "auth"."user_roles",
    "auth"."refresh_tokens",
    "auth"."password_reset_tokens",

    -- Catalog Context
    "catalog"."games",
    "catalog"."game_tags",
    "catalog"."game_game_tags",

    -- Cart Context
    "cart"."wishlist",
    "cart"."shopping_carts",
    "cart"."cart_items",

    -- Checkout Context
    "checkout"."orders",
    "checkout"."order_items",
    "checkout"."game_keys",
    "checkout"."user_purchased_games"
CASCADE;

INSERT INTO "auth"."roles"
    ("role", "description")
VALUES ('USER', 'Common user role');