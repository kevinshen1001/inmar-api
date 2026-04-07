-- V3__fix_user_passwords.sql
-- BCrypt hash of "password" (10 rounds) - verified correct
-- $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi is the well-known
-- test hash for "password", accepted by Spring Security BCryptPasswordEncoder
UPDATE app_user 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE username IN ('admin', 'user');
