update budget
set type = 'Расход'
where type = 'Комиссия';

ALTER TABLE budget ADD COLUMN author_id int references author(id)