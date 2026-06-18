CREATE TABLE IF NOT EXISTS health_logs (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    log_date DATE NOT NULL,
    sleep_hours REAL NOT NULL,
    steps INTEGER NOT NULL,
    mood_score INTEGER NOT NULL CHECK (mood_score BETWEEN 1 AND 10),
    risk_level VARCHAR(20)
);