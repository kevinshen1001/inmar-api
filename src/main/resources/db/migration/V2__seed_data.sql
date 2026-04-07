-- V2__seed_data.sql

-- Locations
INSERT INTO location (name, description) VALUES
    ('Perimeter', 'Perimeter store location'),
    ('Center', 'Center store location')
ON CONFLICT (name) DO NOTHING;

-- Departments under Perimeter
INSERT INTO department (name, description, location_id)
SELECT d.name, d.description, l.id
FROM (VALUES
    ('Bakery', 'Bakery department'),
    ('Deli and Foodservice', 'Deli and Foodservice department'),
    ('Floral', 'Floral department'),
    ('Seafood', 'Seafood department')
) AS d(name, description)
CROSS JOIN location l WHERE l.name = 'Perimeter'
ON CONFLICT (name, location_id) DO NOTHING;

-- Departments under Center
INSERT INTO department (name, description, location_id)
SELECT d.name, d.description, l.id
FROM (VALUES
    ('Dairy', 'Dairy department'),
    ('Frozen', 'Frozen department'),
    ('GM', 'General Merchandise department'),
    ('Grocery', 'Grocery department')
) AS d(name, description)
CROSS JOIN location l WHERE l.name = 'Center'
ON CONFLICT (name, location_id) DO NOTHING;

-- Categories: Perimeter > Bakery
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Bakery Bread', 'Bakery Bread category'),
    ('In Store Bakery', 'In Store Bakery category')
) AS c(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Bakery' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Perimeter > Deli and Foodservice
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Self Service Deli Cold', 'Self Service Deli Cold category'),
    ('Service Deli', 'Service Deli category')
) AS c(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Perimeter > Floral
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Bouquets and Cut Flowers', 'Bouquets and Cut Flowers category'),
    ('Gifts', 'Gifts category'),
    ('Plants', 'Plants category')
) AS c(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Floral' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Perimeter > Seafood
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Frozen Shellfish', 'Frozen Shellfish category'),
    ('Other Seafood', 'Other Seafood category')
) AS c(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Center > Dairy
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Cheese', 'Cheese category'),
    ('Cream or Creamer', 'Cream or Creamer category'),
    ('Cultured', 'Cultured category'),
    ('Refrigerated Baking', 'Refrigerated Baking category')
) AS c(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Dairy' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Center > Frozen
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Frozen Bake', 'Frozen Bake category'),
    ('Frozen Breakfast', 'Frozen Breakfast category'),
    ('Frozen Desserts or Fruit and Toppings', 'Frozen Desserts category'),
    ('Frozen Juice', 'Frozen Juice category')
) AS c(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Center > GM
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Audio Video', 'Audio Video category'),
    ('Housewares', 'Housewares category'),
    ('Insect and Rodent', 'Insect and Rodent category'),
    ('Kitchen Accessories', 'Kitchen Accessories category'),
    ('Laundry', 'Laundry category')
) AS c(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Categories: Center > Grocery
INSERT INTO category (name, description, department_id)
SELECT c.name, c.description, dep.id
FROM (VALUES
    ('Baking Ingredients', 'Baking Ingredients category'),
    ('Spices', 'Spices category'),
    ('Stuffing Products', 'Stuffing Products category')
) AS c(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Grocery' AND dep.location_id = l.id
ON CONFLICT (name, department_id) DO NOTHING;

-- Subcategories: Perimeter > Bakery > Bakery Bread
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Bagels', 'Bagels subcategory'),
    ('Baking or Breading Products', 'Baking or Breading Products subcategory'),
    ('English Muffins or Biscuits', 'English Muffins or Biscuits subcategory'),
    ('Flatbreads', 'Flatbreads subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Bakery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Bakery Bread' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Perimeter > Bakery > In Store Bakery
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Breakfast Cake or Sweet Roll', 'Breakfast Cake subcategory'),
    ('Cakes', 'Cakes subcategory'),
    ('Pies', 'Pies subcategory'),
    ('Seasonal', 'Seasonal subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Bakery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'In Store Bakery' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Perimeter > Deli and Foodservice > Self Service Deli Cold
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Beverages', 'Beverages subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Self Service Deli Cold' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Perimeter > Deli and Foodservice > Service Deli
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Cheese All Other', 'Cheese All Other subcategory'),
    ('Cheese American', 'Cheese American subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Service Deli' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Perimeter > Floral
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Bouquets and Cut Flowers', 'Bouquets and Cut Flowers subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Floral' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Bouquets and Cut Flowers' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Gifts', 'Gifts subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Floral' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Gifts' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Plants', 'Plants subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Floral' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Plants' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Perimeter > Seafood
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Frozen Shellfish', 'Frozen Shellfish subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Shellfish' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('All Other Seafood', 'All Other Seafood subcategory'),
    ('Prepared Seafood Entrees', 'Prepared Seafood Entrees subcategory'),
    ('Seafood Salads', 'Seafood Salads subcategory'),
    ('Smoked Fish', 'Smoked Fish subcategory'),
    ('Seafood Breading Sauces Dips', 'Seafood Breading Sauces Dips subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Perimeter'
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Other Seafood' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Dairy > Cheese
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Cheese Sauce', 'Cheese Sauce subcategory'),
    ('Specialty Cheese', 'Specialty Cheese subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Dairy' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Cheese' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Dairy > Cream or Creamer
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Dairy Alternative Creamer', 'Dairy Alternative Creamer subcategory'),
    ('Whipping Creams', 'Whipping Creams subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Dairy' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Cream or Creamer' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Dairy > Cultured
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Cottage Cheese', 'Cottage Cheese subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Dairy' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Cultured' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Dairy > Refrigerated Baking
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Refrigerated Breads', 'Refrigerated Breads subcategory'),
    ('Refrigerated English Muffins and Biscuits', 'Refrigerated English Muffins and Biscuits subcategory'),
    ('Refrigerated Hand Held Sweets', 'Refrigerated Hand Held Sweets subcategory'),
    ('Refrigerated Pie Crust', 'Refrigerated Pie Crust subcategory'),
    ('Refrigerated Sweet Breakfast Baked Goods', 'Refrigerated Sweet Breakfast Baked Goods subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Dairy' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Refrigerated Baking' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Frozen > Frozen Bake
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Bread or Dough Products Frozen', 'Bread or Dough Products Frozen subcategory'),
    ('Breakfast Cake or Sweet Roll Frozen', 'Breakfast Cake or Sweet Roll Frozen subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Bake' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Frozen > Frozen Breakfast
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Frozen Breakfast Entrees', 'Frozen Breakfast Entrees subcategory'),
    ('Frozen Breakfast Sandwich', 'Frozen Breakfast Sandwich subcategory'),
    ('Frozen Egg Substitutes', 'Frozen Egg Substitutes subcategory'),
    ('Frozen Syrup Carriers', 'Frozen Syrup Carriers subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Breakfast' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Frozen > Frozen Desserts
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Pies Frozen', 'Pies Frozen subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Desserts or Fruit and Toppings' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Frozen > Frozen Juice
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Frozen Apple Juice', 'Frozen Apple Juice subcategory'),
    ('Frozen Fruit Drink Mixers', 'Frozen Fruit Drink Mixers subcategory'),
    ('Frozen Fruit Juice All Other', 'Frozen Fruit Juice All Other subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Juice' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > GM > Audio Video
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Audio', 'Audio subcategory'),
    ('Video DVD', 'Video DVD subcategory'),
    ('Video VHS', 'Video VHS subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Audio Video' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > GM > Housewares
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Bedding', 'Bedding subcategory'),
    ('Candles', 'Candles subcategory'),
    ('Collectibles and Gifts', 'Collectibles and Gifts subcategory'),
    ('Flashlights', 'Flashlights subcategory'),
    ('Frames', 'Frames subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Housewares' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > GM > Insect and Rodent
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Indoor Repellants or Traps', 'Indoor Repellants subcategory'),
    ('Outdoor Repellants or Traps', 'Outdoor Repellants subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Insect and Rodent' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > GM > Kitchen Accessories
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Kitchen Accessories', 'Kitchen Accessories subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Kitchen Accessories' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > GM > Laundry
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Bleach Liquid', 'Bleach Liquid subcategory'),
    ('Bleach Powder', 'Bleach Powder subcategory'),
    ('Fabric Softener Liquid', 'Fabric Softener Liquid subcategory'),
    ('Fabric Softener Sheets', 'Fabric Softener Sheets subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Laundry' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Grocery > Baking Ingredients
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Dry or Canned Milk', 'Dry or Canned Milk subcategory'),
    ('Food Coloring', 'Food Coloring subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Grocery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Baking Ingredients' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Grocery > Spices
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES
    ('Salt Cooking or Edible or Seasoned', 'Salt Cooking subcategory'),
    ('Salt Substitute', 'Salt Substitute subcategory'),
    ('Seasoning Dry', 'Seasoning Dry subcategory')
) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Grocery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Spices' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- Subcategories: Center > Grocery > Stuffing Products
INSERT INTO subcategory (name, description, category_id)
SELECT s.name, s.description, cat.id
FROM (VALUES ('Stuffing Products', 'Stuffing Products subcategory')) AS s(name, description)
JOIN location l ON l.name = 'Center'
JOIN department dep ON dep.name = 'Grocery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Stuffing Products' AND cat.department_id = dep.id
ON CONFLICT (name, category_id) DO NOTHING;

-- SKU data (using Perimeter as the canonical spelling)
INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT
    '1', 'SKUDESC1',
    l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Bakery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Bakery Bread' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Bagels' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '2', 'SKUDESC2', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Self Service Deli Cold' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Beverages' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '3', 'SKUDESC3', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Floral' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Bouquets and Cut Flowers' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Bouquets and Cut Flowers' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '4', 'SKUDESC4', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Service Deli' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Cheese All Other' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '5', 'SKUDESC5', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Bake' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Bread or Dough Products Frozen' AND sub.category_id = cat.id
WHERE l.name = 'Center'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '7', 'SKUDESC7', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Audio Video' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Audio' AND sub.category_id = cat.id
WHERE l.name = 'Center'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '8', 'SKUDESC8', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'GM' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Audio Video' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Video DVD' AND sub.category_id = cat.id
WHERE l.name = 'Center'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '10', 'SKUDESC10', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Shellfish' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Frozen Shellfish' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '11', 'SKUDESC11', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Other Seafood' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'All Other Seafood' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '12', 'SKUDESC12', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Other Seafood' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Prepared Seafood Entrees' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '13', 'SKUDESC13', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Seafood' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Other Seafood' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Seafood Salads' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '14', 'SKUDESC14', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Bakery' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Bakery Bread' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Bagels' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '15', 'SKUDESC15', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Self Service Deli Cold' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Beverages' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '16', 'SKUDESC16', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Floral' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Bouquets and Cut Flowers' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Bouquets and Cut Flowers' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '17', 'SKUDESC17', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Deli and Foodservice' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Service Deli' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Cheese All Other' AND sub.category_id = cat.id
WHERE l.name = 'Perimeter'
ON CONFLICT (sku_code) DO NOTHING;

INSERT INTO sku (sku_code, name, location_id, department_id, category_id, subcategory_id)
SELECT '18', 'SKUDESC18', l.id, dep.id, cat.id, sub.id
FROM location l
JOIN department dep ON dep.name = 'Frozen' AND dep.location_id = l.id
JOIN category cat ON cat.name = 'Frozen Bake' AND cat.department_id = dep.id
JOIN subcategory sub ON sub.name = 'Bread or Dough Products Frozen' AND sub.category_id = cat.id
WHERE l.name = 'Center'
ON CONFLICT (sku_code) DO NOTHING;

-- Default users (passwords are bcrypt encoded: admin123, user123)
INSERT INTO app_user (username, password, role)
VALUES
    ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN'),
    ('user',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER')
ON CONFLICT (username) DO NOTHING;
