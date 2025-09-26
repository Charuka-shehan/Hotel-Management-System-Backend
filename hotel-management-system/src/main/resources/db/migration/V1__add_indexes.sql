CREATE INDEX idx_booking_dates ON bookings(check_in_date, check_out_date);
CREATE INDEX idx_room_availability ON rooms(available);

-- Create database
CREATE DATABASE IF NOT EXISTS hotel_management_system;
USE hotel_management_system;

-- Grant permissions (if needed)
GRANT ALL PRIVILEGES ON hotel_management_system.* TO 'root'@'localhost';
FLUSH PRIVILEGES;