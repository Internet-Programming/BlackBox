<?php 
require (__DIR__.'/DBConnect.php');
$data =json_decode(file_get_contents('php://input'),true);

$carNumber = $data['Num'];
$password = $data['PW'];

$DBconn = connectDB();

$result = selectInfoDataSignIn ($DBconn, $carNumber, $password);


closeDB($DBconn);

$i = 0;
while($data = mysqli_fetch_array($result)) {
	$i++;
}


$rtn = array();
$rtn['result'] = true;
$rtn['data'] = array();

if ($i > 0) {
	$rtn['data']['success'] = true;
} else {
	$rtn['data']['success'] = false;
}


echo json_encode($rtn,JSON_UNESCAPED_UNICODE);
exit;

 ?>