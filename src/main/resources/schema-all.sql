DROP TABLE EventDetails IF EXISTS;

CREATE TABLE EventDetails  (
    event_id VARCHAR(20),
    event_duration VARCHAR(20),
    event_type VARCHAR(20),
    event_host VARCHAR(20),
    event_alert  VARCHAR(2)
);