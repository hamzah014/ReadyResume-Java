/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


document.addEventListener('DOMContentLoaded', function () {
    console.log("oi");
    setTheadClass();
    //j_idt26:userid
    console.log(document.getElementById('uid').value);
    var id = document.getElementById('uid').value;
    document.getElementsByClassName('usercls')[0].value = id;
    document.getElementsByClassName('birthcls')[0].placeholder = "Your selected date will be shown here.";

    var currentbirth = new Date(document.getElementsByClassName("birthcls")[0].value);

    var dates = currentbirth.getDate();
    var years = currentbirth.getFullYear();
    var months = currentbirth.getMonth() + 1;

    if (dates < 10) {
        newdates = "0" + dates;
    } else {
        newdates = dates;
        console.log('date lebehhh');
    }

    if (months < 10) {
        newmonths = "0" + months;
    } else {
        newmonths = months;
    }

    setbirth = years + "-" + newmonths + "-" + newdates;
    console.log(setbirth);
    document.getElementById("calendar").value = setbirth;

});

function selectDateTime() {

    console.log("oi datetime");
    datetime = new Date(document.getElementById("calendar").value);

    dates = datetime.getDate();
    years = datetime.getFullYear();
    months = datetime.getMonth() + 1;

    times = "00:00:00";

    fullformat = months + "/" + dates + "/" + years + " " + times;

    document.getElementsByClassName("birthcls")[0].value = fullformat;

}

function setTheadClass() {

    console.log("set thead class");
    var elm = document.getElementsByTagName('thead')
    var length = elm.length;
    for (var i = 0; i < length; i++) {
        elm[i].className = elm[i].className + "thead-dark";
    }


}
