<?php
require 'init.php';

$email = $_POST["email"];

if ($con) {
    $sql = "SELECT * FROM user_info WHERE email='$email'";
    $result = mysqli_query($con, $sql);

    if (mysqli_num_rows($result) > 0) {
        $newPassword = generateRandomPassword(); // Tạo mật khẩu mới ngẫu nhiên
        $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT); // Mã hóa mật khẩu mới

        $updateSql = "UPDATE user_info SET password='$hashedPassword' WHERE email='$email'";
        $updateResult = mysqli_query($con, $updateSql);

        if ($updateResult) {
            sendResetPasswordEmail($email, $newPassword);
            $status = "ok";
        } else {
            $status = "failed";
        }
    } else {
        $status = "failed";
    }
} else {
    $status = "failed";
}

mysqli_close($con);

echo json_encode(array('status' => $status));

function generateRandomPassword() {
    $characters = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    $password = '';
    $length = 8;

    for ($i = 0; $i < $length; $i++) {
        $index = rand(0, strlen($characters) - 1);
        $password .= $characters[$index];
    }

    return $password;
}

function sendResetPasswordEmail($email, $newPassword) {
    $to = $email;
    $subject = "Reset Password";
    $message = "Your new password: " . $newPassword;
    $headers = "From: tuanda222@gmail.com" . "\r\n" .
               "Reply-To: tuanda222@gmail.com" . "\r\n" .
               "X-Mailer: PHP/" . phpversion();
    mail($to, $subject, $message, $headers);
}
?>