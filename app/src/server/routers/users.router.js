const express = require('express');
const usersRouter = express.Router();
require('dotenv').config();

const {Users} = require('../models');
const {uploadImg} = require('../middlewares/upload/uploadImage');
const {isCreated, isExistId, validateInput, isExistEmail, isCreatedUsername} = require('../middlewares/validation/validation');
const { createUser,
        login,
        updatePremium,
        changePassword,
        updateUser,
        uploadImage,
        passwordRecovery,
        getAllUsers,
        getUserById } = require('../controllers/users.controller');

// create Account 
usersRouter.post('/register', validateInput(['email', 'password', 'username', 'almaMater']), isCreated(Users), isCreatedUsername(Users), createUser);
// login
usersRouter.post('/login', validateInput(['username', 'password']), login);
// password recovery
usersRouter.post('/recover-password', validateInput(['email']), isExistEmail(Users), passwordRecovery);
// update to premium
usersRouter.put('/profiles/update-premium/:id', isExistId(Users), updatePremium);
// change password
usersRouter.put('/profiles/password/:id', validateInput(['password', 'newPassword']), isExistId(Users), changePassword);
// update user
usersRouter.put('/profiles/:id', isExistId(Users), isCreated(Users), updateUser);
// upload profile image
usersRouter.put('/profiles/change-profile-image/:id', uploadImg.single('avatars'), uploadImage);
// get all users
usersRouter.get("/", getAllUsers);
// get user by id
usersRouter.get("/:id", isExistId(Users), getUserById);

module.exports = usersRouter;