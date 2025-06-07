/**
 * E-Commerce Site Form Validation
 * This script handles validation for registration, delivery, and password reset forms
 */
$(document).ready(function () {
    console.log("Form validation script loaded");

    // Custom validation methods
    $.validator.addMethod("lettersonly", function (value, element) {
        return this.optional(element) || /^[a-zA-Z\s]+$/.test(value);
    }, "Letters only please");

    $.validator.addMethod("space", function (value, element) {
        return this.optional(element) || value.indexOf(" ") < 0;
    }, "No spaces allowed");

    $.validator.addMethod("numericOnly", function (value, element) {
        return this.optional(element) || /^[0-9]+$/.test(value);
    }, "Numbers only please");

    $.validator.addMethod("all", function (value, element) {
        return this.optional(element) || /^[a-zA-Z0-9\s.,#-]+$/.test(value);
    }, "Invalid characters");

    // Common validation settings
    const validationSettings = {
        errorElement: "div",
        errorPlacement: function (error, element) {
            error.addClass("invalid-feedback");
            error.insertAfter(element);
        },
        highlight: function (element) {
            $(element).addClass("is-invalid").removeClass("is-valid");
        },
        unhighlight: function (element) {
            $(element).addClass("is-valid").removeClass("is-invalid");
        }
    };

    // Registration form
    if ($("#userRegister").length > 0) {
        console.log("Registration form found, initializing validation");
        $("#userRegister").validate($.extend({
            rules: {
                name: { required: true, lettersonly: true },
                email: { required: true, space: true, email: true },
                mobileNumber: {
                    required: true, space: true,
                    numericOnly: true, minlength: 10, maxlength: 12
                },
                password: { required: true, space: true, minlength: 6 },
                cpassword: {
                    required: true, space: true,
                    equalTo: '#userRegister input[name="password"]'
                },
                address: { required: true, all: true },
                city: { required: true },
                state: { required: true },
                pincode: { required: true, space: true, numericOnly: true }
            },
            messages: {
                name: { required: "Name is required", lettersonly: "Please enter a valid name" },
                email: {
                    required: "Email is required", space: "No spaces allowed",
                    email: "Enter a valid email"
                },
                mobileNumber: {
                    required: "Mobile number is required", space: "No spaces allowed",
                    numericOnly: "Digits only", minlength: "Min 10 digits", maxlength: "Max 12 digits"
                },
                password: {
                    required: "Password is required", space: "No spaces allowed",
                    minlength: "At least 6 characters"
                },
                cpassword: {
                    required: "Confirm your password", space: "No spaces allowed",
                    equalTo: "Passwords do not match"
                },
                address: { required: "Address required", all: "Invalid characters in address" },
                city: { required: "City is required" },
                state: { required: "State is required" },
                pincode: {
                    required: "Pincode is required", space: "No spaces allowed",
                    numericOnly: "Digits only"
                }
            },
            submitHandler: function (form) {
                console.log("Registration form validated successfully");
                form.submit();
            }
        }, validationSettings));

        $("#userRegister").on("submit", function (e) {
            if (!$(this).valid()) {
                e.preventDefault();
                console.log("Registration form validation failed");
            }
        });
    }

    // Delivery form
    if ($("#deliveryForm").length > 0) {
        console.log("Delivery form found, initializing validation");
        $("#deliveryForm").validate($.extend({
            rules: {
                firstName: { required: true, lettersonly: true },
                lastName: { required: true, lettersonly: true },
                email: { required: true, space: true, email: true },
                mobileNo: {
                    required: true, space: true,
                    numericOnly: true, minlength: 10, maxlength: 12
                },
                address: { required: true, all: true },
                city: { required: true, space: true },
                state: { required: true },
                pincode: { required: true, space: true, numericOnly: true },
                paymentType: { required: true }
            },
            messages: {
                firstName: { required: "First name required", lettersonly: "Letters only" },
                lastName: { required: "Last name required", lettersonly: "Letters only" },
                email: {
                    required: "Email required", space: "No spaces allowed",
                    email: "Enter a valid email"
                },
                mobileNo: {
                    required: "Mobile required", space: "No spaces allowed",
                    numericOnly: "Digits only", minlength: "Min 10 digits", maxlength: "Max 12 digits"
                },
                address: { required: "Address required", all: "Invalid address format" },
                city: { required: "City required", space: "No spaces allowed" },
                state: { required: "State required" },
                pincode: {
                    required: "Pincode required", space: "No spaces allowed",
                    numericOnly: "Digits only"
                },
                paymentType: { required: "Select payment type" }
            },
            submitHandler: function (form) {
                console.log("Delivery form validated successfully");
                form.submit();
            }
        }, validationSettings));

        $("#placeOrderBtn").on("click", function (e) {
            e.preventDefault();
            const $form = $("#deliveryForm");
            console.log("Place order button clicked, validating form...");
            if ($form.valid()) {
                console.log("Order form validated successfully, submitting...");
                $form.submit();
            } else {
                console.log("Order form validation failed");
                const firstError = $form.find(".is-invalid:first");
                if (firstError.length) {
                    $("html, body").animate({
                        scrollTop: firstError.offset().top - 100
                    }, 500);
                }
            }
        });

        $("#deliveryForm").on("submit", function (e) {
            if (!$(this).valid()) {
                e.preventDefault();
                console.log("Delivery form validation failed");
            }
        });
    }

    // Reset password form
    if ($("#resetPassword").length > 0) {
        console.log("Reset password form found, initializing validation");
        $("#resetPassword").validate($.extend({
            rules: {
                password: { required: true, space: true, minlength: 6 },
                confirmPassword: {
                    required: true, space: true,
                    equalTo: '#resetPassword input[name="password"]'
                }
            },
            messages: {
                password: {
                    required: "Password required", space: "No spaces allowed",
                    minlength: "At least 6 characters"
                },
                confirmPassword: {
                    required: "Confirm your password", space: "No spaces allowed",
                    equalTo: "Passwords do not match"
                }
            },
            submitHandler: function (form) {
                console.log("Reset password form validated successfully");
                form.submit();
            }
        }, validationSettings));

        $("#resetPassword").on("submit", function (e) {
            if (!$(this).valid()) {
                e.preventDefault();
                console.log("Reset password form validation failed");
            }
        });
    }
});
