<?php
	require (__DIR__.'/DBConnect.php');

	$rtn['data'] =json_decode(file_get_contents('php://input'),true);

	$rtn['result'] = true;
	$carNumber = $rtn['data']['Num'];
	$URI = $rtn['data']['URI'];
	/*
	$fileSize = filesize($URI);
	$pathParts = pathinfo($URI);
	$fileName = $pathParts['basename'];
	$extension = $pathParts['extension'];
	*/

	
	$filePath = $URI;
	$file = "/".$URI;
	$file_size = filesize($file);
	$filename = urlencode($filePath);
	

	print_r($filePath ."<br>");
	print_r($_SERVER['DOCUMENT_ROOT'] ."<br>");
	print_r($file_size ."<br>");
	
	echo  json_encode($rtn,JSON_UNESCAPED_UNICODE);
	/*
	if (is_file($file)) // 파일이 존재하면
	{
		// 파일 전송용 HTTP 헤더를 설정합니다.
		if(strstr($HTTP_USER_AGENT, "MSIE 5.5"))
		{
			header("Content-Type: doesn/matter");
			header("Content-Length: ".$file_size);
			header("Content-Disposition: filename=".$filename);
			header("Content-Transfer-Encoding: binary");
			header("Pragma: no-cache");
			header("Expires: 0");
		}
		else
		{
			header("Content-type: file/unknown");
			header("Content-Disposition: attachment; filename=".$filename);
			header("Content-Transfer-Encoding: binary");
			header("Content-Length: ".$file_size);
			header("Content-Description: PHP3 Generated Data");
			header("Pragma: no-cache");
			header("Expires: 0");
		}
		//파일을 열어서, 전송합니다.
		$fp = fopen($file, "rb");
		if (!fpassthru($fp))
		fclose($fp);
	}
	*/
?>