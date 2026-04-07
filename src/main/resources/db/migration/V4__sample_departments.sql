-- V4__sample_departments.sql
-- Insert the 10 sample departments under both Perimeter and Center locations
-- so they are discoverable via the /api/v1/department/search endpoint.
-- We attach them to the "Center" location as a general store-wide grouping.

INSERT INTO department (name, description, location_id)
SELECT d.name, d.description, l.id
FROM (VALUES
    ('Produce',         'Fresh fruits, vegetables, and herbs.'),
    ('Meat & Poultry',  'Also called the Butcher department, this includes fresh cuts of beef, chicken, and pork.'),
    ('Dairy',           'Refrigerated items like milk, eggs, cheese, yogurt, and butter.'),
    ('Bakery',          'Freshly baked breads, pastries, cakes, and cookies.'),
    ('Deli',            'Sliced lunch meats, cheeses, and prepared salads or sandwiches.'),
    ('Frozen Foods',    'Items kept in freezers, such as frozen pizzas, Ice cream, and frozen vegetables.'),
    ('Seafood',         'Fresh and frozen fish, shrimp, and other shellfish.'),
    ('Grocery',         'Non-perishable shelf items like pasta, canned goods, cereal, and snacks.'),
    ('Beverages',       'Soft drinks, juices, bottled water, and sometimes ice beer, wine, or spirits.'),
    ('Health & Beauty & dry (HBC)', 'Personal care items like shampoo, toothpaste, and over-the-counter medicines.')
) AS d(name, description)
CROSS JOIN location l
WHERE l.name = 'Center'
ON CONFLICT (name, location_id) DO UPDATE
    SET description = EXCLUDED.description;
