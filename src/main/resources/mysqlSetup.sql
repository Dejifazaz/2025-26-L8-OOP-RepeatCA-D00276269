-- Drop and recreate database from scratch
DROP DATABASE IF EXISTS gospel_catalogue;

CREATE DATABASE gospel_catalogue;

USE gospel_catalogue;

-- Create singer table first (no dependencies)
CREATE TABLE singer (
    singer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    vocal_type VARCHAR(50) NOT NULL,
    home_church VARCHAR(100) NOT NULL,
    year_active INT NOT NULL
);

-- Create song table (no dependencies)
CREATE TABLE song (
    song_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    artist VARCHAR(100) NOT NULL,
    album VARCHAR(100) NOT NULL,
    year_released INT NOT NULL,
    duration_seconds INT NOT NULL,
    bpm DOUBLE NOT NULL
);

-- Create performance table last (depends on singer and song)
CREATE TABLE performance (
    performance_id INT AUTO_INCREMENT PRIMARY KEY,
    singer_id INT NOT NULL,
    song_id INT NOT NULL,
    church_name VARCHAR(100) NOT NULL,
    performance_date DATE NOT NULL,
    FOREIGN KEY (singer_id) REFERENCES singer(singer_id),
    FOREIGN KEY (song_id) REFERENCES song(song_id)
);

-- Seed singer data
INSERT INTO singer (name, vocal_type, home_church, year_active) VALUES
('Theophilus Sunday', 'Tenor', 'Daystar Christian Centre Lagos', 2010),
('Nathaniel Bassey', 'Tenor', 'RCCG City of David Lagos', 2005),
('Florence Kisiwaa', 'Soprano', 'Kingdom Ambassador Centre Hackney London', 2026),
('Kaestrings', 'Baritone', 'House on the Rock Abuja', 2015),
('Ryan Ofei', 'Tenor', 'Hillsong Church Sydney', 2012),
('Sewa', 'Soprano', 'Campus Rush Church Ghana', 2014),
('Bridge Music', 'Ensemble', 'Bridge Church London', 2016),
('Steffany Gretzinger', 'Soprano', 'Bethel Church Redding', 2010),
('Elevation Worship', 'Ensemble', 'Elevation Church Charlotte', 2007),
('Nathaniel Bassey', 'Tenor', 'Hillsong Africa Johannesburg', 2018);

-- Seed song data
INSERT INTO song (title, artist, album, year_released, duration_seconds, bpm) VALUES
('Warrior', 'Theophilus Sunday', 'The Fathers House', 2018, 312, 78.0),
('Imela', 'Nathaniel Bassey', 'Imela', 2013, 425, 65.0),
('Hallowed Be Your Name', 'Florence Kisiwaa ft. Chantelle Rutendo and Jesse Nyakudya', 'The Secret Place Vol.1', 2026, 298, 72.0),
('Na You', 'Kaestrings', 'Gratitude', 2021, 267, 85.0),
('You Are Good', 'Ryan Ofei', 'You Are Good', 2017, 334, 90.0),
('Fill Us', 'Sewa ft. Worship Culture', 'Campus Rush Sessions', 2020, 289, 80.0),
('Holy Spirit', 'Bridge Music', 'Bridge Sessions', 2018, 356, 68.0),
('Pieces', 'Steffany Gretzinger', 'The Undoing', 2014, 378, 70.0),
('Graves Into Gardens', 'Elevation Worship', 'Graves Into Gardens', 2020, 412, 75.0),
('This Is Amazing Grace', 'Elevation Worship', 'Only King Forever', 2013, 298, 95.0);

-- Seed performance data
INSERT INTO performance (singer_id, song_id, church_name, performance_date) VALUES
(1, 1, 'Daystar Christian Centre Lagos', '2023-01-15'),
(2, 2, 'RCCG City of David Lagos', '2023-02-20'),
(3, 3, 'Kingdom Ambassador Centre Hackney London', '2026-06-05'),
(4, 4, 'House on the Rock Abuja', '2023-04-05'),
(5, 5, 'Hillsong Church Sydney', '2023-05-12'),
(6, 6, 'Campus Rush Church Ghana', '2023-06-18'),
(7, 7, 'Bridge Church London', '2023-07-22'),
(8, 8, 'Bethel Church Redding', '2023-08-30'),
(9, 9, 'Elevation Church Charlotte', '2023-09-14'),
(10, 10, 'Hillsong Africa Johannesburg', '2023-10-25');