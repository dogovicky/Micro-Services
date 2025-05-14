-- Alter users table, drop column created at
ALTER TABLE users
DROP COLUMN created_at;

-- Add dayOfTheWeek (String), month (String), year (Integer) columns, time (String) and date (LocalDate)
ALTER TABLE users ADD COLUMN day_of_the_week VARCHAR(20);
ALTER TABLE users ADD COLUMN "month" VARCHAR(20);
ALTER TABLE users ADD COLUMN "year" INTEGER;
ALTER TABLE users ADD COLUMN "time" VARCHAR(20);
ALTER TABLE users ADD COLUMN "date" DATE;