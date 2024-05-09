CREATE TABLE letter_counter
(
);

CREATE TABLE year_company_counters
(
    year_id        BIGINT NOT NULL,
    letter_counter INTEGER,
    company_id     BIGINT NOT NULL,
    CONSTRAINT pk_year_company_counters PRIMARY KEY (year_id, company_id)
);

ALTER TABLE year_company_counters
    ADD CONSTRAINT fk_year_company_counters_on_company FOREIGN KEY (company_id) REFERENCES company (id);

ALTER TABLE year_company_counters
    ADD CONSTRAINT fk_year_company_counters_on_year FOREIGN KEY (year_id) REFERENCES year (id);

ALTER TABLE insurance_slip
    ALTER COLUMN amount TYPE DECIMAL USING (amount::DECIMAL);

ALTER TABLE tax_payment_slip
    ALTER COLUMN amount TYPE DECIMAL USING (amount::DECIMAL);