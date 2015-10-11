<?php
$rtn = array();
$rtn['result'] = true;
$rtn['data'] = $_POST['js']['b'];
echo json_encode($rtn);
exit;
?>