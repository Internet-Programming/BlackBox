<?php 
require (__DIR__.'/DBConnect.php');


$carNumber = $_POST['Num'];
$password = $_POST['PW'];

$DBconn = connectDB();

$result = selectInfoData ($carNumber, $password);


closeDB($DBconn);

$i = 0;
while($data = mysql_fetch_array($result)) {
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


echo json_encode($rtn);
exit;

 ?>