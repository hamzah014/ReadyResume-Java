/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



function selectDateTime(id,clsname) {

    console.log("oi datetime");
    datetime = new Date(document.getElementById(id).value);

    dates = datetime.getDate();
    years = datetime.getFullYear();
    months = datetime.getMonth() + 1;

    times = "00:00:00";

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

    fullformat = newmonths + "/" + newdates + "/" + years + " " + times;
    console.log(fullformat);
    
    document.getElementsByClassName(clsname)[0].value = fullformat;

}
