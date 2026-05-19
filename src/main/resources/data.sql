INSERT INTO users (
    full_name,
    email,
    password,
    skills,
    bio,
    active,
    role
) VALUES
(
    'Admin User',
    'admin@elms.com',
    '$2a$10$abcdefghijklmnopqrstuv',
    'Administration, System Management',
    'Platform administrator',
    true,
    'ADMIN'
),
(
    'Dr. Ravi Kumar',
    'instructor@elms.com',
    '$2a$10$abcdefghijklmnopqrstuv',
    'Java, Spring Boot, Angular',
    'Senior Software Architect',
    true,
    'INSTRUCTOR'
),
(
    'Arjun Sharma',
    'student@elms.com',
    '$2a$10$abcdefghijklmnopqrstuv',
    'Java, Python',
    'Learning Full Stack',
    true,
    'STUDENT'
);
