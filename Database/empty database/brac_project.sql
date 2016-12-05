-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jun 02, 2016 at 06:02 AM
-- Server version: 5.6.24
-- PHP Version: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `brac_project`
--

DELIMITER $$
--
-- Functions
--
CREATE DEFINER=`root`@`localhost` FUNCTION `COUNT_VOTES`(`pid` INT) RETURNS int(11)
BEGIN

DECLARE upvotes INT;
DECLARE downvotes INT;

SET upvotes = (SELECT COUNT(*) FROM vote WHERE post_id = pid and vote_type = 1);

SET downvotes = (SELECT COUNT(*) FROM vote WHERE post_id = pid and vote_type = -1);

RETURN upvotes-downvotes;

END$$

CREATE DEFINER=`root`@`localhost` FUNCTION `DISTANCE`(`LAT1` FLOAT, `LON1` FLOAT, `LAT2` FLOAT, `LON2` FLOAT) RETURNS float
BEGIN
    DECLARE THETA FLOAT;
    DECLARE DIST FLOAT;
    DECLARE MILES FLOAT;
   
    SET THETA = LON1 - LON2;
    SET DIST = SIN(RADIANS(LAT1)) * SIN(RADIANS(LAT2)) + COS(RADIANS(LAT1)) * COS(RADIANS(LAT2)) * COS(RADIANS(THETA));
    SET DIST = ACOS(DIST);
    SET DIST = DEGREES(DIST);
    SET MILES = DIST * 60 * 1.1515;
    SET MILES = MILES * 1609.344;
   
    RETURN MILES;

    END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` int(11) NOT NULL,
  `admin_name` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `bracadmin`
--

CREATE TABLE IF NOT EXISTS `bracadmin` (
  `id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bracadmin`
--

INSERT INTO `bracadmin` (`id`, `name`, `password`) VALUES
(2, 'brac', '827ccb0eea8a706c4c34a16891f84e7b');

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE IF NOT EXISTS `category` (
  `categoryId` int(11) NOT NULL,
  `name` varchar(256) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`categoryId`, `name`) VALUES
(-1, 'Others'),
(1, 'Occupied Footpath'),
(2, 'Open Dustbin'),
(3, 'Open Manhole'),
(4, 'Electric Wires'),
(5, 'Waterlogging'),
(6, 'Risky Intersection'),
(7, 'No Street Light'),
(8, 'Crime Prone Area'),
(9, 'Broken Road'),
(10, 'Wrong Way Traffic');

-- --------------------------------------------------------

--
-- Table structure for table `comment`
--

CREATE TABLE IF NOT EXISTS `comment` (
  `comment_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `text` varchar(1023) DEFAULT NULL,
  `time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE IF NOT EXISTS `location` (
  `location_id` int(11) NOT NULL,
  `lat` float NOT NULL,
  `lon` float NOT NULL,
  `street_number` varchar(31) DEFAULT NULL,
  `route` varchar(127) DEFAULT NULL,
  `neighbourhood` varchar(127) DEFAULT NULL,
  `sublocality` varchar(127) DEFAULT NULL,
  `locality` varchar(127) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `logs`
--

CREATE TABLE IF NOT EXISTS `logs` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `cat_id` int(11) NOT NULL,
  `time` datetime NOT NULL,
  `log_type` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `prev_status` int(11) DEFAULT NULL,
  `changed_status` int(11) NOT NULL,
  `cat_name` varchar(256) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `post`
--

CREATE TABLE IF NOT EXISTS `post` (
  `post_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `category` int(11) NOT NULL COMMENT '-1: MISCELLANEOUS, 0: ILLEGAL OCCUPATION, 1: DAMAGED STREET LIGHT, 2: GARBAGE ON ROAD',
  `image` blob NOT NULL,
  `video` varchar(1023) NOT NULL,
  `time` datetime DEFAULT NULL,
  `informal_location` varchar(511) DEFAULT NULL,
  `text` varchar(1023) DEFAULT NULL,
  `actual_location_id` int(11) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0 : PENDING, 1: VERIFIED , 2 : REJECTED, 3: SOLVED',
  `rating_change` int(11) DEFAULT '0',
  `flag` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `subadmin`
--

CREATE TABLE IF NOT EXISTS `subadmin` (
  `id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `suggestedcategory`
--

CREATE TABLE IF NOT EXISTS `suggestedcategory` (
  `id` int(11) NOT NULL,
  `name` varchar(256) DEFAULT NULL,
  `count` int(11) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int(11) NOT NULL,
  `user_name` varchar(127) NOT NULL,
  `email` varchar(128) NOT NULL,
  `user_rating` int(11) NOT NULL DEFAULT '500',
  `password` varchar(255) NOT NULL,
  `is_verified` int(11) NOT NULL DEFAULT '0',
  `ver_code` varchar(128) DEFAULT NULL,
  `con_reject` int(11) NOT NULL DEFAULT '0' COMMENT 'unused',
  `last_suspension` datetime DEFAULT NULL COMMENT 'unused',
  `suspension_days` int(11) NOT NULL DEFAULT '0' COMMENT 'unused',
  `is_suspended` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vote`
--

CREATE TABLE IF NOT EXISTS `vote` (
  `vote_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `vote_type` int(11) NOT NULL COMMENT '-1 : Downvote , +1 : Upvote'
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`), ADD UNIQUE KEY `admin_name` (`admin_name`);

--
-- Indexes for table `bracadmin`
--
ALTER TABLE `bracadmin`
  ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`categoryId`);

--
-- Indexes for table `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`comment_id`), ADD KEY `post_id_comment` (`post_id`), ADD KEY `user_id_comment` (`user_id`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`location_id`);

--
-- Indexes for table `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`log_id`);

--
-- Indexes for table `post`
--
ALTER TABLE `post`
  ADD PRIMARY KEY (`post_id`), ADD KEY `user_id_post` (`user_id`), ADD KEY `loc_id_post` (`actual_location_id`);

--
-- Indexes for table `subadmin`
--
ALTER TABLE `subadmin`
  ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `suggestedcategory`
--
ALTER TABLE `suggestedcategory`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `vote`
--
ALTER TABLE `vote`
  ADD PRIMARY KEY (`vote_id`), ADD KEY `user_id_vote` (`user_id`), ADD KEY `post_id_vote` (`post_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `bracadmin`
--
ALTER TABLE `bracadmin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `categoryId` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=16;
--
-- AUTO_INCREMENT for table `comment`
--
ALTER TABLE `comment`
  MODIFY `comment_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
  MODIFY `location_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=18;
--
-- AUTO_INCREMENT for table `logs`
--
ALTER TABLE `logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=97;
--
-- AUTO_INCREMENT for table `post`
--
ALTER TABLE `post`
  MODIFY `post_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=52;
--
-- AUTO_INCREMENT for table `subadmin`
--
ALTER TABLE `subadmin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `suggestedcategory`
--
ALTER TABLE `suggestedcategory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=19;
--
-- AUTO_INCREMENT for table `vote`
--
ALTER TABLE `vote`
  MODIFY `vote_id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=26;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `comment`
--
ALTER TABLE `comment`
ADD CONSTRAINT `post_id_comment` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
ADD CONSTRAINT `user_id_comment` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `post`
--
ALTER TABLE `post`
ADD CONSTRAINT `loc_id_post` FOREIGN KEY (`actual_location_id`) REFERENCES `location` (`location_id`),
ADD CONSTRAINT `user_id_post` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `vote`
--
ALTER TABLE `vote`
ADD CONSTRAINT `post_id_vote` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
ADD CONSTRAINT `user_id_vote` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
