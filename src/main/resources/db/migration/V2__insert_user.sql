INSERT INTO public.user(id, birthdate, email, firstname, gender, lastname, password, is_temp_password, role)
VALUES (nextVal('user_seq'), null, 'admin@mail.com', 'admin', 'MALE', 'admin', '$2a$10$jKrfZ3jhp3UI1SsQgdWQjOwNfiTek9VaqPY37zxpPBrWrxM2vt5xm', false, 'ADMIN');

