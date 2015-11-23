<?php
header('Content-Type: text/html; charset=UTF-8');
require (__DIR__.'/DBConnect.php');
$rtn = array();
$rtn['data'] =json_decode(file_get_contents('php://input'),true);

$carNumber = $rtn['data']['Num'];
$password = $rtn['data']['PW'];

print_r($carNumber);
print_r($password);

//print_r($password."  23455");

$DBconn = connectDB();

$result = selectInfoData ($carNumber, $password);



$i = 0;
while($data = mysql_fetch_array($result)) {
	$i++;
}

if ($i > 0) {
	$rtn['result'] = false;
} else {
	insertInfoData ($carNumber, $password);

	$rtn['result'] = true;
}


closeDB($DBconn);


echo json_encode($rtn);

exit;
?>