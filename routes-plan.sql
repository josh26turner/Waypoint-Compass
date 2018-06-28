--Getting the list of tables
SELECT * FROM sqlite_master WHERE type='table' AND name <> 'wayPoints'

--Creating the new route
CREATE TABLE route_name (
	position	INT			PRIMARY KEY,
    name    	VARCHAR(30) UNIQUE NOT NULL,
    longitude   DOUBLE      NOT NULL,
    latitude    DOUBLE      NOT NULL
)