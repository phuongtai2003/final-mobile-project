const {Folder} = require('../models');

const createFolder = async (req, res) => {
    const {folderNameEnglish, folderNameVietnamese} = req.body;
    const user = req.user;
    try {
        const folder = await Folder.create({folderNameEnglish, folderNameVietnamese});
        res.status(200).json({folder});
    }catch (error) {
        res.status(500).json({error});
    }
}

const createTopicInFolder = async (req, res) => {
    const folderId = req.params.id || req.query.id;
    const {topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietnamese, isPublic} = req.body;
    const user = req.user;
    try {
        const folder = await Folder.create({folderNameEnglish, folderNameVietNamese});
        res.status(200).json({folder});
    }catch (error) {
        res.status(500).json({error});
    }
}

const addTopicToFolder = async (req, res) => {
}

const getFolderById = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        const folder = await Folder.findById(id);
        res.status(200).json({folder});
    } catch (error) {
        res.status(500).json({ error });
    }
}

const getAllFolders = async (req, res) => {
    try {
        const folders = await Folder.find();
        if(folders.length === 0){
            res.status(404).json({error: "Folders not found"});
            return;
        }
        res.status(200).json({folders});
    } catch (error) {
        res.status(500).json({ error });
    }
}

module.exports = {
    createTopicInFolder,
    getFolderById,
    getAllFolders
}