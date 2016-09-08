<?php

$ftp_server = "gosiwon777.cafe24.com";//ok9030
$ftp_server_port = 21;
$ftp_user_name = "gosiwon777";
$ftp_user_pass = "gosiwon779";

$foldername = $_POST['foldername'];
$filename = $_POST['filename'];

$source_file = $_FILES['image']['tmp_name'];

$dir = "hair/".$foldername;
$dest_file = $dir."/".$filename;

// set up basic connection
$conn_id = ftp_connect($ftp_server, $ftp_server_port);

// login with username and password
$login_result = ftp_login($conn_id, $ftp_user_name, $ftp_user_pass);

//checks for image directory 
ftp_mkdir($conn_id, $dir);
// if (ftp_mkdir($conn_id, $dir)) {
//  echo "successfully created $dir\n";
// } else {
//  echo "There was a problem while creating $dir\n";
// }
//echo $dest_file;

$upload = ftp_put($conn_id, $dest_file, $source_file, FTP_BINARY); 

//checks to see if the file was successfully uploaded 
if(!$upload) { 
    echo "{result: 1}"; 
} 
else { 
    echo "{result: 0}"; 
}


// close the connection
ftp_close($conn_id);



?>