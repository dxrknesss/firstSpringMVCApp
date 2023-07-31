function validateFormAge() {
    let age = document.forms["form"]["fage"].value;
    let text;
    if (isNaN(age) || age < 0 || age > 110) {
        text = "You were trying to input an invalid age!";
    }
    document.getElementById("age_err").innerText = text;
}

function validate() {
    let re = /(\.jpg|\.png|\.JPG|\.PNG)$/i;
    let fname = document.getElementById("fileToUpload").value.toLowerCase();
    if (!re.exec(fname)) {
        alert("Your file should be a valid .png or .jpg image!");
        return false;
    }
    return true;
}