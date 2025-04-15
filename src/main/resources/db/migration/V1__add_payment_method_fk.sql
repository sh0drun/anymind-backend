ALTER TABLE payments ADD COLUMN payment_method_id UUID;

UPDATE payments p
SET payment_method_id = pm.id
FROM payment_methods pm
WHERE p.payment_method = pm.payment_method;

ALTER TABLE payments ALTER COLUMN payment_method_id SET NOT NULL;

ALTER TABLE payments DROP COLUMN payment_method;

ALTER TABLE payments
ADD CONSTRAINT fk_payment_method
FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id);