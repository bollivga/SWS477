/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var req;
var isIE;

function doMGetName() {
    
    console.log("hi");
    document.getElementById("result").innerHTML= "hi";
        passField = document.getElementById("pass");
    nameField = document.getElementById("mname");
    houseField = document.getElementById("mhouse");
    locField = document.getElementById("mloc");
        var url = "" + "?passphrase=\"" + escape(passField.value)+"\"&name=\""+escape(nameField.value)+"\"";
        req = initRequest();
        req.open("GET", url, true);
        req.onreadystatechange = callback;
        req.send(null);
        var table = '<table><thead><tr><th>Name</th><th>House</th><th>Location</th></tr></thead>\
    <tbody>'+"x"+'</tbody>\
</table>';
    
}

function initRequest() {
    if (window.XMLHttpRequest) {
        if (navigator.userAgent.indexOf('MSIE') != -1) {
            isIE = true;
        }
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        isIE = true;
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}