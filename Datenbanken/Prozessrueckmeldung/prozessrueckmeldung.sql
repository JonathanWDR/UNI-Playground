
--CREATE TABLES
CREATE TABLE Operator (
	operator_id INT PRIMARY KEY,
	vorname VARCHAR(20),
	nachname VARCHAR(20)
);

CREATE TABLE Material (
	nummer INT,
	variante INT,
	PRIMARY KEY (nummer, variante)
);
CREATE TABLE Prozessschritt (
	prozess_id INT PRIMARY KEY,
	text VARCHAR(50),
	position INT
);

CREATE TABLE Prozessrueckmeldung (
	prozessauftrag INT PRIMARY KEY,
	datum TIMESTAMP,
	operator_id INT,
	nummer INT,
	variante INT,
	FOREIGN KEY (operator_id) REFERENCES Operator(operator_id),
	FOREIGN KEY (nummer, variante) REFERENCES Material(nummer, variante)
);

CREATE TABLE prozessrueckmeldung_prozessschritt (
	prozessauftrag INT,
	prozessschritt INT,
	menge INT,
	PRIMARY KEY (prozessauftrag, prozessschritt),
	FOREIGN KEY (prozessauftrag) REFERENCES Prozessrueckmeldung(prozessauftrag),
	FOREIGN KEY (prozessschritt) REFERENCES Prozessschritt(prozess_id)
);




--INSERT TABLES
INSERT INTO Operator VALUES
(1, 'Kevin', 'Schraubenzieher'),
(2, 'Alois', 'Sicherheitslücke'),
(3, 'Gabi', 'Gaskocher'),
(4, 'Ingo', 'Isolierband');

INSERT INTO Material VALUES
(100, 1),
(100, 2),
(200, 1),
(300, 7);

INSERT INTO Prozessrueckmeldung VALUES
(10, '2025-07-01 08:15:00', 1, 100, 1),
(11, '2025-07-01 09:00:00', 2, 100, 2),
(12, '2025-07-01 10:30:00', 3, 200, 1),
(13, '2025-07-02 06:45:00', 4, 300, 7);

INSERT INTO Prozessschritt VALUES
(1, 'Magnetisieren mit Laserschwert', 1),
(2, 'Thermische Verwirrung', 2),
(3, 'Schütteln, nicht rühren', 3),
(4, 'Rückwärts filtrieren', 4);

INSERT INTO prozessrueckmeldung_prozessschritt VALUES
(10, 1, 42),
(10, 2, 5),
(11, 3, 7),
(12, 1, 23),
(12, 4, 99),
(13, 3, 13),
(13, 4, 0);

-- Gesamten Prozessschrit auslesen
SELECT * FROM Prozessrueckmeldung AS pr
JOIN Operator AS o
ON pr.operator_id = o.operator_id
JOIN Material AS m
ON pr.nummer = m.nummer AND pr.variante = m.variante
JOIN prozessrueckmeldung_prozessschritt AS prps
ON pr.prozessauftrag = prps.prozessauftrag
JOIN Prozessschritt AS ps
ON prps.prozessschritt = ps.prozess_id;












