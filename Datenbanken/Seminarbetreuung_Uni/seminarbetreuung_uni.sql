CREATE TABLE Student(
student_id INT PRIMAY KEY,
vorname VARCHAR(20),
nachname VARCHAR(20)
);

CREATE TABLE Professor(
professor_id INT PRIMARY KEY,
vorname VARCHAR(20),
nachname VARCHAR(20)
);

CREATE TABLE Seminarthema(
thema_id INT PRIMARY KEY,
titel VARCHAR(50)
);

CREATE TABLE Betreuen(
student_id INT,
professor_id INT,
thema_id INT,
note DECIMAL(3,2),
PRIMARY KEY (student_id, professor_id, thema_id),
FOREIGN KEY (student_id) REFERENCES Student(student_id),
FOREIGN KEY (professor_id) REFERENCES Student(professor_id),
FOREIGN KEY (thema_id) REFERENCES Student(thema_id)
);

SELECT * FROM Student;

INSERT INTO Student (student_id, name, nachname) VALUES
(1, "Max", "Muster"),
(2, 'Julia', 'Schmidt'),
(3, 'Lukas', 'Meier'),
(4, 'Anna', 'Fischer'),
(5, 'Tim', 'Becker');

SELECT * FROM Professor;

INSERT INTO Professor (professor_id, vorname, nachname) VALUES
(1, 'Albert', 'Witzigmann'),
(2, 'Marie', 'Käseblatt'),
(3, 'Heinz', 'Krümel'),
(4, 'Susi', 'Sorglos'),
(5, 'Dirk', 'Durchblick');

INSERT INTO Seminarthema (thema_id, titel) VALUES
(1, 'Die Philosophie der Kaffeepause – Produktivität zwischen Bohne und Büro'),
(2, 'Memes als moderne Form politischer Kommunikation'),
(3, 'Warum Einhörner in Business-Präsentationen erfolgreicher wirken'),
(4, 'Der Einfluss von Katzenvideos auf das Stressempfinden von Studierenden'),
(5, 'Postapokalyptische Projektplanung – Wenn der Strom mal ausfällt');


INSERT INTO Betreuen (student_id, professor_id, thema_id, note) VALUES
(1, 4, 2, 1.7);

SELECT * FROM Betreuen;

-- Jetzt die Königsklasse mit JOIN
SELECT b.student_id, s.vorname, s.nachname, b.professor_id, p.vorname, p.nachname, b.thema_id, t.titel, b.note
FROM Betreuen AS b
JOIN Student AS s ON b.student_id = s.student_id
JOIN Professor AS p ON b.professor_id = p.professor_id
JOIN Seminarthema AS t ON b.thema_id = t.thema_id;

-- Hier SELECT mit paar coolen Filtern
SELECT * FROM Student
WHERE nachname LIKE 'M%'
ORDER BY vorname ASC
LIMIT 3;

-- Durchschnitt von Werten bilden
SELECT professor_id, AVG(note) AS durchschnitt_note
FROM Betreuen
GROUP BY professor_id;

-- Unterabfragen (engl Subqueries)
SELECT * FROM Betreuen
WHERE note = (SELECT MIN(note) FROM Betreuen);

-- Einfache Logik
SELECT student_id, note,
	CASE
		WHEN note <= 1.3 THEN 'Sehr gut'
		WHEN note <= 2.0 THEN 'Gut'
		WHEN note <= 3.0 THEN 'Befriedigend'
		ELSE 'Ausbaufähig'
	END AS bewertung
FROM Betreuen;

-- Update
UPDATE Student SET nachname = 'Mustermann' WHERE student_id = 1;

-- Löschen
DELETE FROM Betreuen WHERE note > 4.0;

-- CONSTRAINTS & NULL-Werte
CREATE TABLE Beispiel(
	id INT PRIMARY KEY,
	wert INT (wert >= 0),
	status VARCHAR(10) DEFAULT 'neu' NOT NULL
);

-- Views (gespeicherte Abfragen)
CREATE VIEW Betreuungsübersicht AS
SELECT s.vorname, s.nachname, t.titel, b.note
FROM Betreuen b
JOIN Student s ON b.student_id = s.student_id
JOIN Seminarthema t ON b.thema_id = t.thema_id;

-- Transaktionen (BEGIN, COMMIT)
BEGIN;
UPDATE Konten SET betrag = betrag - 100 WHERE id = 1;
UPDATE Konten SET betrag = betrag + 100 WHERE id = 2;
COMMIT;




