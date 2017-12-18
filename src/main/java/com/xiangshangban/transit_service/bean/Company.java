package com.xiangshangban.transit_service.bean;

public class Company {
    private String company_id;

    private String company_name;

    private String company_english_name;

    private String company_abbreviation_name;

    private String company_address_detail;

    private String company_phone;

    private String company_email;

    private String company_postalcode;

    private String company_business_license;

    private String juridical_person;

    private String brought_accoun;

    private String registered_capital;

    private String join_date;

    private String company_state;

    private String registration_number;

    private String business_registration_number;

    private String uniform_credit_code;

    private String type_enterprise;

    private String taxpayer_registration_number;

    private String industry_type;

    private String begin_business_time;

    private String end_business_time;

    private String dateof_approval;

    private String registration_authority;

    private String registered_address;

    private String company_creat_time;

    private String company_area;

    private String company_code;

    private String company_logo;

    private String company_approve;

    private String company_personal_name;

    private String user_name;

    private String company_no;

	private String company_type;
	
	private String defaultOption;

	public Company(String company_id, String company_name, String company_english_name,
			String company_abbreviation_name, String company_address_detail, String company_phone, String company_email,
			String company_postalcode, String company_business_license, String juridical_person, String brought_accoun,
			String registered_capital, String join_date, String company_state, String registration_number,
			String business_registration_number, String uniform_credit_code, String type_enterprise,
			String taxpayer_registration_number, String industry_type, String begin_business_time,
			String end_business_time, String dateof_approval, String registration_authority, String registered_address,
			String company_creat_time, String company_area, String company_code, String company_logo,
			String company_approve, String company_personal_name, String user_name, String company_no,
			String company_type) {
        this.company_id = company_id;
        this.company_name = company_name;
        this.company_english_name = company_english_name;
        this.company_abbreviation_name = company_abbreviation_name;
        this.company_address_detail = company_address_detail;
        this.company_phone = company_phone;
        this.company_email = company_email;
        this.company_postalcode = company_postalcode;
        this.company_business_license = company_business_license;
        this.juridical_person = juridical_person;
        this.brought_accoun = brought_accoun;
        this.registered_capital = registered_capital;
        this.join_date = join_date;
        this.company_state = company_state;
        this.registration_number = registration_number;
        this.business_registration_number = business_registration_number;
        this.uniform_credit_code = uniform_credit_code;
        this.type_enterprise = type_enterprise;
        this.taxpayer_registration_number = taxpayer_registration_number;
        this.industry_type = industry_type;
        this.begin_business_time = begin_business_time;
        this.end_business_time = end_business_time;
        this.dateof_approval = dateof_approval;
        this.registration_authority = registration_authority;
        this.registered_address = registered_address;
        this.company_creat_time = company_creat_time;
        this.company_area = company_area;
        this.company_code = company_code;
        this.company_logo = company_logo;
        this.company_approve = company_approve;
        this.company_personal_name = company_personal_name;
        this.user_name = user_name;
        this.company_no = company_no;
		this.company_type = company_type;
    }

    public Company() {
        super();
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id == null ? null : company_id.trim();
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name == null ? null : company_name.trim();
    }

    public String getCompany_english_name() {
        return company_english_name;
    }

    public void setCompany_english_name(String company_english_name) {
        this.company_english_name = company_english_name == null ? null : company_english_name.trim();
    }

    public String getCompany_abbreviation_name() {
        return company_abbreviation_name;
    }

    public void setCompany_abbreviation_name(String company_abbreviation_name) {
        this.company_abbreviation_name = company_abbreviation_name == null ? null : company_abbreviation_name.trim();
    }

    public String getCompany_address_detail() {
        return company_address_detail;
    }

    public void setCompany_address_detail(String company_address_detail) {
        this.company_address_detail = company_address_detail == null ? null : company_address_detail.trim();
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone == null ? null : company_phone.trim();
    }

    public String getCompany_email() {
        return company_email;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email == null ? null : company_email.trim();
    }

    public String getCompany_postalcode() {
        return company_postalcode;
    }

    public void setCompany_postalcode(String company_postalcode) {
        this.company_postalcode = company_postalcode == null ? null : company_postalcode.trim();
    }

    public String getCompany_business_license() {
        return company_business_license;
    }

    public void setCompany_business_license(String company_business_license) {
        this.company_business_license = company_business_license == null ? null : company_business_license.trim();
    }

    public String getJuridical_person() {
        return juridical_person;
    }

    public void setJuridical_person(String juridical_person) {
        this.juridical_person = juridical_person == null ? null : juridical_person.trim();
    }

    public String getBrought_accoun() {
        return brought_accoun;
    }

    public void setBrought_accoun(String brought_accoun) {
        this.brought_accoun = brought_accoun == null ? null : brought_accoun.trim();
    }

    public String getRegistered_capital() {
        return registered_capital;
    }

    public void setRegistered_capital(String registered_capital) {
        this.registered_capital = registered_capital == null ? null : registered_capital.trim();
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date == null ? null : join_date.trim();
    }

    public String getCompany_state() {
        return company_state;
    }

    public void setCompany_state(String company_state) {
        this.company_state = company_state == null ? null : company_state.trim();
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number == null ? null : registration_number.trim();
    }

    public String getBusiness_registration_number() {
        return business_registration_number;
    }

    public void setBusiness_registration_number(String business_registration_number) {
        this.business_registration_number = business_registration_number == null ? null : business_registration_number.trim();
    }

    public String getUniform_credit_code() {
        return uniform_credit_code;
    }

    public void setUniform_credit_code(String uniform_credit_code) {
        this.uniform_credit_code = uniform_credit_code == null ? null : uniform_credit_code.trim();
    }

    public String getType_enterprise() {
        return type_enterprise;
    }

    public void setType_enterprise(String type_enterprise) {
        this.type_enterprise = type_enterprise == null ? null : type_enterprise.trim();
    }

    public String getTaxpayer_registration_number() {
        return taxpayer_registration_number;
    }

    public void setTaxpayer_registration_number(String taxpayer_registration_number) {
        this.taxpayer_registration_number = taxpayer_registration_number == null ? null : taxpayer_registration_number.trim();
    }

    public String getIndustry_type() {
        return industry_type;
    }

    public void setIndustry_type(String industry_type) {
        this.industry_type = industry_type == null ? null : industry_type.trim();
    }

    public String getBegin_business_time() {
        return begin_business_time;
    }

    public void setBegin_business_time(String begin_business_time) {
        this.begin_business_time = begin_business_time == null ? null : begin_business_time.trim();
    }

    public String getEnd_business_time() {
        return end_business_time;
    }

    public void setEnd_business_time(String end_business_time) {
        this.end_business_time = end_business_time == null ? null : end_business_time.trim();
    }

    public String getDateof_approval() {
        return dateof_approval;
    }

    public void setDateof_approval(String dateof_approval) {
        this.dateof_approval = dateof_approval == null ? null : dateof_approval.trim();
    }

    public String getRegistration_authority() {
        return registration_authority;
    }

    public void setRegistration_authority(String registration_authority) {
        this.registration_authority = registration_authority == null ? null : registration_authority.trim();
    }

    public String getRegistered_address() {
        return registered_address;
    }

    public void setRegistered_address(String registered_address) {
        this.registered_address = registered_address == null ? null : registered_address.trim();
    }

    public String getCompany_creat_time() {
        return company_creat_time;
    }

    public void setCompany_creat_time(String company_creat_time) {
        this.company_creat_time = company_creat_time == null ? null : company_creat_time.trim();
    }

    public String getCompany_area() {
        return company_area;
    }

    public void setCompany_area(String company_area) {
        this.company_area = company_area == null ? null : company_area.trim();
    }

    public String getCompany_code() {
        return company_code;
    }

    public void setCompany_code(String company_code) {
        this.company_code = company_code == null ? null : company_code.trim();
    }

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo == null ? null : company_logo.trim();
    }

    public String getCompany_approve() {
        return company_approve;
    }

    public void setCompany_approve(String company_approve) {
        this.company_approve = company_approve == null ? null : company_approve.trim();
    }

    public String getCompany_personal_name() {
        return company_personal_name;
    }

    public void setCompany_personal_name(String company_personal_name) {
        this.company_personal_name = company_personal_name == null ? null : company_personal_name.trim();
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name == null ? null : user_name.trim();
    }

    public String getCompany_no() {
        return company_no;
    }

    public void setCompany_no(String company_no) {
        this.company_no = company_no == null ? null : company_no.trim();
    }

	public String getCompany_type() {
		return company_type;
	}

	public void setCompany_type(String company_type) {
		this.company_type = company_type;
	}

	public String getDefaultOption() {
		return defaultOption;
	}

	public void setDefaultOption(String defaultOption) {
		this.defaultOption = defaultOption;
	}

}