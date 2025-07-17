CREATE TABLE Konto (
	iban INT PRIMARY KEY,
	kunden_id INT,
	kontostand DECIMAL(10,2)
);

INSERT INTO Konto (iban, kunden_id, kontostand) VALUES 
(1234, 1111, 1000.00), 
(5678, 2222, 40230.42);

SELECT * FROM Konto;

-- Vom Konto 1234 werden 100€ auf das Konto 5678 überwiesen
BEGIN TRANSACTION;
UPDATE Konto
SET kontostand = kontostand - 100
WHERE iban = '1234';

UPDATE Konto
SET kontostand = kontostand + 100
WHERE iban = '5678';

COMMIT;