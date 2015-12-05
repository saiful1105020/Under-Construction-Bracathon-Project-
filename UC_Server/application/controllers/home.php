<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Home extends CI_Controller {
	 
	 public function __construct()		//DONE
     {
          parent::__construct();
		  
		  $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  
		  $this->load->model('post_model');
     }
	 
	public function index()
	{
		
	}
	
	public function getAllPosts()
	{
		$location_id=$_GET['locationId'];
		$result=$this->post_model->get_all_post($location_id);
		
		$posts=array();
		
		foreach($result as $r)
		{
			/*
			echo '<dt><strong>Technician Image:</strong></dt><dd>'
			 . '<img src="data:image/jpeg;base64,' . base64_encode($r['image']) . '" width="290" height="290">'
			 . '</dd>';
			 */
			
			$post['postId']=$r['post_id'];
			//$post['user_id']=$r['user_id'];
			$post['category']=$r['category'];
			$post['time']=$r['time'];
			$post['informalLocation']=$r['informal_location'];
			$post['problemDescription']=$r['text'];
			$post['image']=base64_encode($r['image']);
			//$post['actual_location_id']=$r['actual_location_id'];
			//$post['status']=$r['status'];
			//$post['rating_change']=$r['rating_change'];
			$post['userName']=$r['user_name'];
			$post['userRating']=$r['user_rating'];
			
			$temp = $this->post_model->get_vote_count($post['postId']);
			$post['upCount']=$temp['upvotes'];
			$post['downCount']=$temp['downvotes'];
			array_push($posts,$post);
		}
		
		echo json_encode($posts);
	}
}
