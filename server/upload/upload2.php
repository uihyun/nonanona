<?php

include('Thumbnail.class.php');


function uploadImage($dest_dir, $dest_file, $source_file) {
	$ftp_server = "nuums2016.cafe24.com";
	$ftp_server_port = 21;
	$ftp_user_name = "nuums2016";
	$ftp_user_pass = "nuumsnuums2016!";

	//$fp = fopen($source_file, 'r');  
	
	// set up basic connection
	$conn_id = ftp_connect($ftp_server, $ftp_server_port);
	// login with username and password
	$login_result = ftp_login($conn_id, $ftp_user_name, $ftp_user_pass);
	//checks for image directory 
	ftp_mkdir($conn_id, $dest_dir);

	$upload = ftp_put($conn_id, $dest_file, $source_file, FTP_BINARY); 


	// close the connection
	ftp_close($conn_id);
	//fclose($fp);  
	
	//checks to see if the file was successfully uploaded 
	if(!$upload) { 
	    return false;
	} 
	else { 
	    return true;
	} 

}


$foldername = $_POST['foldername'];
$filename = $_POST['filename'];
$type = $_POST['type'];

$source_file = $_FILES['image']['tmp_name'];

$dir = "nuums/".$foldername;
$dest_file = $dir."/".$filename;

$isSuccess = false;


//$isSuccess = uploadImage($dir, $dir."/".$filename."org", $source_file);


if (move_uploaded_file($source_file, $filename)) {
				
	Thumbnail::setOption('export', EXPORT_JPG);
	//echo $filename."</BR>";
	
	$filename_s = "";
	$filename_m = "";
	
	//if($type != 'AVATAR') {
		$filename_s = Thumbnail::create($filename,
				  240, 240,
				  SCALE_EXACT_FIT,
				  Array(
					  'savepath' => '%FILENAME%_s.%EXT%'
				  ));


		$filename_m = Thumbnail::create($filename,
				  720, 720,
				  SCALE_EXACT_FIT,
				  Array(
					  'savepath' => '%FILENAME%_m.%EXT%'
				  ));
	//}

    if($type == 'CHAT')
        $isSuccess = uploadImage($dir, $dir."/".$filename, $filename);
    else
	    $isSuccess = true;


	if($filename_s != "") {
		if($isSuccess)
			$isSuccess = uploadImage($dir, $dir."/".$filename_s, $filename_s);
		if($isSuccess)
		 	unlink($filename_s);
	}


	if($filename_m != "") {
		if($isSuccess)
			$isSuccess = uploadImage($dir, $dir."/".$filename_m, $filename_m);
		if($isSuccess)
		 	unlink($filename_m);
	}

	 unlink($filename);
	

} else {
	$isSuccess = false;
}

if($isSuccess) {
	echo "{result: 0}"; 
} else {
	echo "{result: 1}"; 
}



?>