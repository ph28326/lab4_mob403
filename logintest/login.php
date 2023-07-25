<?php

$user_name=$_POST["user_name"];
$user_password=$_POST["password"];

require'init.php';

if($con){
    $sql="SELECT name FROM user_info WHERE user_name='$user_name' and password='$user_password'";
    $result=mysqli_query($con,$sql);
    if(mysqli_num_rows($result)>0){
        $row=mysqli_fetch_assoc($result);
        $status="ok";
        $result_code=1;
        $name=$row['name'];
        echo json_encode(array('status'=>$status,'result_code'=>$result_code,'name'=>$name));
    } else{
        $status="ok";
        $result_code=0;
        echo json_encode(array('status'=>$status,'result_code'=>$result_code));
    }
}else{
    $status="failed";
    echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
}
mysqli_close($con);
?>