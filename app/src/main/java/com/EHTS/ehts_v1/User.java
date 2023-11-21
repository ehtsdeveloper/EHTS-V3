package com.EHTS.ehts_v1;

//this class hold the users data
public class User {
        private String fullName;
        private String email;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


}
