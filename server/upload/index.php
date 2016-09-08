<?php
include('Thumbnail.class.php');




function uploadImage($dest_dir, $dest_file, $source_file) {
	$ftp_server = "gosiwon777.cafe24.com";//ok9030
	$ftp_server_port = 21;
	$ftp_user_name = "gosiwon777";
	$ftp_user_pass = "gosiwon779";

	//$fp = fopen($source_file, 'r');  
	
	// set up basic connection
	$conn_id = ftp_connect($ftp_server, $ftp_server_port);
	// login with username and password
	$login_result = ftp_login($conn_id, $ftp_user_name, $ftp_user_pass);
	//checks for image directory 
	ftp_mkdir($conn_id, $dest_dir);

	$upload = ftp_put($conn_id, $dest_file, $source_file, FTP_BINARY); 

	//checks to see if the file was successfully uploaded 
	if(!$upload) { 
	    return false;
	} 
	else 
	{ 
	    return true;
	} 

	// close the connection
	ftp_close($conn_id);
	//fclose($fp);  
}


if(isset($_POST["btnSubmit"])) {
	if(!empty($_FILES['fileAttach'])) {

		$uploadDir = '';
		$orgFilename = $uploadDir.(basename($_FILES["fileAttach"]['name']));
		
		if (move_uploaded_file($_FILES["fileAttach"]['tmp_name'], $orgFilename)) {
				
			//Thumbnail::setOption('debug', true);
			Thumbnail::setOption('export', EXPORT_JPG);
			$savepath = Thumbnail::create($orgFilename,
					  120, 120,
					  SCALE_EXACT_FIT,
					  Array(
						  'savepath' => '%PATH%%FILENAME%_s.%EXT%'
					  ));

			$dir = "hair/test";

			uploadImage($dir, $dir."/".$orgFilename, $orgFilename);
			uploadImage($dir, $dir."/".$savepath, $savepath);


			unlink($orgFilename);
			unlink($savepath);




		} else {

		}

	}	
}

?>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Crop Center</title>
<link href="style.css" rel="stylesheet" type="text/css" />
</head>

<body>
<form action="#" method="post" name="frmThis" id="frmThis" enctype="multipart/form-data">
<a href="index.php">Refresh this page ....</a><br />
<br />


<br />
<input name="fileAttach" type="file" id="fileAttach" size="30" /><input name="btnSubmit" type="submit" id="btnSubmit" value="Upload" />
</form>
</body>
</html>
