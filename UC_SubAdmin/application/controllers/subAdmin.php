<?php

/**
*	LAST MODIFIED : 29-06-2015 04:02 PM
*/

defined('BASEPATH') OR exit('No direct script access allowed');

class subAdmin extends CI_Controller {
	  
	 public function __construct()
     {
          parent::__construct();
		  
		  /**
		  *	Load Libraries , Models and Helpers
		  */
		  
          $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  
		if(!isset($_SESSION["subAdminName"]))
		{
			redirect('/home', 'refresh');
		}
		  
		//load models
		$this->load->model('subAdminModel');
		$this->load->model('post_model');
		$this->load->model('log_model');
		//$data['current_nav']='home';
		//$this->load->view('templates/header',$data);
		
		$this->load->view('templates/header2');
     }
	 
	/**
	*	[ADMIN HOME PAGE]
	*/
	
	public function index()			
	{
		redirect('subAdmin/search','refresh');
	}
	
	public function search()
	{
		if(!isset($_POST['location_select']))
		{
			$data['is_set']=false;
			$data['n_loc']=$this->subAdminModel->get_all_locations();
			$data['catData'] = $this->post_model->get_all_categories();

			$search_key=array();
			$search_key['location']="ANY";
			$search_key['category']="ANY";
			$search_key['duration']="ANY";
			
			$data['search_key'] = $search_key;
			
			$this->load->view('subAdminHome',$data);
		}
		else
		{
			$data['n_loc']=$this->subAdminModel->get_all_locations();
			$data['catData'] = $this->post_model->get_all_categories();

			$search_key=array();
			
			if(isset($_POST['location_select'])) $search_key['location']=$_POST['location_select'];
			else $search_key['location']="ANY";
			
			if(isset($_POST['category_select'])) $search_key['category']=$_POST['category_select'];
			else $search_key['category']="ANY";
			
			if(isset($_POST['duration_select'])) $search_key['duration']=$_POST['duration_select'];
			else $search_key['duration']="ANY";
			
			$search_key['status']="ANY";
			
			//load last 20 posts using the search_key combination
			$data['is_set']=true;
			$posts=$this->subAdminModel->search_post($search_key);
			$data['posts']=array();
			
			//echo '<br><br><br>Test';
			//print_r($search_key);
			
			foreach($posts as $p)
			{
				$post=$p;
				$temp=$this->post_model->get_vote_count($p['post_id']);
				$post['up_votes']=$temp['upvotes'];
				$post['down_votes']=$temp['downvotes'];
				$post['user_name']=$this->post_model->get_user_name($p['user_id']);
				$post['user_rating']=$this->post_model->get_current_rating($post['user_id']);
				
				$post['cat_name'] = $this->post_model->get_category_name($p['category']);

				$post['location']=$this->post_model->get_location($p['actual_location_id']);
				
				
				array_push($data['posts'],$post);
			}
			$data['search_key'] = $search_key;
			$this->load->view('subAdminHome',$data);
			//print_r($data);
		}
	}
	
	public function take_action($post_id)
	{
		if(isset($_POST['action'])) $action = $_POST['action'];
		else redirect('subAdmin/search','refresh');
		
		//echo '<br><br><br>'.$action;
		
		
		//CHANGE FLAG STATUS OF THE POST

		$logData = array();
		
		$logData['user_id']=$_SESSION["subAdmin_id"];
		$logData['cat_id']=$this->post_model->getCategoryId($post_id);
		$logData['log_type']=10;
		$logData['post_id']=$post_id;
		$logData['changed_status']=$_POST['action'];
		$logData['cat_name']=$this->post_model->get_category_name($logData['cat_id']);
				
		$this->log_model->insert_log($logData);

		$this->post_model->update_flag_status($post_id,$action);
		
		
		/**
		//CALCULATE RATING CHANGE
		$change=0;
		if($action==0)
		{
			$change = 5;
		}
		else if($action==1)
		{
			$change= -5;
		}
		
		$this->post_model->update_rating_change($post_id,$change);
		*/
		
		$data['success']=true;
		if($action==0) $data['success_message']="The post has been unflagged";
		else if($action==1) $data['success_message']="The post has been marked as flagged";
		$this->load->view('status_message',$data);
		
	}
	
	//--------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------//
	
	/**
	*	Admin Logs out
	*/
	
	public function logout()	
	{
		$logData = array();
		
		$logData['user_id']=$_SESSION["subAdmin_id"];
		$logData['cat_id']=-1;
		$logData['log_type']=9;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']='';
				
		
		$this->log_model->insert_log($logData);

		//$this->session->sess_destroy();	//!Stop Session 
		
		unset($_SESSION["subAdmin_id"]);
		unset($_SESSION["subAdminName"]);
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
	
	/**
	neamul
	*/
	public function showBarGraph()
	{
		$catIds = $this->post_model->get_category_ids();
		$cats = array();
		$pCount = array();
		$solvedCount = array();
		$i = 0;
		foreach($catIds as $id)
		{
			$newCat[0] = "Avoid This";
			$newCat[1] = $this->post_model->get_category_name($id);
			
			$count = $this->post_model->category_problem_count($id);
			
			$sCount = $this->post_model->category_solved_count($id);
			
			$cats[$i]=$newCat;
			$pCount[$i] = $count;
			$solvedCount[$i] = $sCount;
			
			$i++;
		}
		
		//echo '<br><br><br><br><br><br><br>';
		
		//print_r($cats);
		
		//echo '<br>';
		//print_r($pCount);
		
		/*
		$pCounts = $this->post_model->get_problem_count();
		$temp = $this->post_model->get_all_categories();
		$cats = array();
		
		foreach($temp as $t)
		{
			$newCat = array();
			$newCat[0] = $t['categoryId'];
			$newCat[1] = $t['name'];
			
			array_push($newCat, $cats);
		}
		*/
		//print_r($cats);
		
		
		$data['pCounts'] = $pCount;
		$data['cats'] = $cats;
		$data['solvedCounts'] = $solvedCount;
		
		//echo '<br><br><br><br><br><br><br>';
		
		//print_r($solvedCount);

		$locations = array();	
		$lCounts = array();
		$solvedCounts2 = array();


		$nbrhood = $this->post_model->get_all_neighborhoods();
		foreach ($nbrhood as $n) {
			//echo $n['neighbourhood'];
			$element = array();
			//$element['name']=$n['neighbourhood'];
			$element['loc_id']=$this->post_model->get_nbrhd_location_ids($n['neighbourhood']);
			$element['problem_count'] = $this->post_model->get_problem_count_location($element['loc_id']);
			$element['solved_count'] = $this->post_model->get_solved_count_location($element['loc_id']);
			//array_push($data,$element);


			array_push($locations,$n['neighbourhood']);				//
			array_push($lCounts, $element['problem_count']);	//
			array_push($solvedCounts2, $element['solved_count']);

		}

		//$pCounts = $this->post_model->get_problem_count();
		//$cats = array(array(0,'Occupied Footpath'), array(1,'Open Dustbin'), array(2,'Open Manhole'), array(3,'Cluttered Electric Wires'), array(4,'Waterlogging'), array(5,'Risky Intersection'), array(6,'No Street Light'), array(7,'Crime Prone Area'), array(8,'Damaged Road'), array(9,'Wrong Way Traffic'));
		$data['lCounts'] = $lCounts;
		$data['locations'] = $locations;
		$data['solvedCounts2'] = $solvedCounts2;
		
		
		//echo '<br><br><br><br>';
		//print_r($data);
		
		$this->load->view("showBarGraph", $data);
		
	}

	public function test()
	{
		//$data = array();


		$locations = array();	//
		$lCounts = array();	//


		$nbrhood = $this->post_model->get_all_neighborhoods();
		foreach ($nbrhood as $n) {
			//echo $n['neighbourhood'];
			$element = array();
			$element['name']=$n['neighbourhood'];
			$element['loc_id']=$this->post_model->get_location_ids($n['neighbourhood']);
			$element['problem_count'] = $this->post_model->get_problem_count_location($element['loc_id']);
			//array_push($data,$element);


			array_push($locations,$n['neighbourhood']);				//
			array_push($lCounts, $element['problem_count']);	//

		}

		//$pCounts = $this->post_model->get_problem_count();
		//$cats = array(array(0,'Occupied Footpath'), array(1,'Open Dustbin'), array(2,'Open Manhole'), array(3,'Cluttered Electric Wires'), array(4,'Waterlogging'), array(5,'Risky Intersection'), array(6,'No Street Light'), array(7,'Crime Prone Area'), array(8,'Damaged Road'), array(9,'Wrong Way Traffic'));
		$data['lCounts'] = $lCounts;
		$data['locations'] = $locations;
		$this->load->view("showBarGraph", $data);
		//print_r($data);
	}

	

	public function showMap()
	{
		$temp = $this->post_model->get_all_categories();
		$existingCat = array();
		
		foreach($temp as $t)
		{
			$e = array();
			$e['id'] = $t['categoryId'];
			$e['name'] = $t['name'];
			$e['locations'] = $this->post_model->get_category_problem_locations($e['id']);
			
			array_push($existingCat,$e);
		}
		
		//echo '<br><br><br>';
		//print_r($existingCat);
		
		$data['mapData'] = $existingCat;
		
		//unset($data['existingCat'][0]);
		
		$this->load->view('showMap',$data);
	}
	
	/** new func
	*/
	public function showALocation($id)
	{
		//$location['lat'] = 87.091;
		//$location['lon'] = 90.098;
		$data['location'] = $this->post_model->get_post_location($id);
		
		//echo '<br><br><br>';
		//print_r($data['location']);
		
		$this->load->view('showALocation',$data);
	}
	
	public function changePassword()
	{
		$this->load->view('changePassword');
	}
	
	public function changePassAction()
	{
		$oldPassword = md5($_POST['oldPassword']);
		$newPassword = md5($_POST['newPassword']);
		$confirmPassword = md5($_POST['confirmPassword']);
		
		//check password match
		if($this->subAdminModel->checkPassword($_SESSION['subAdminName'],$oldPassword)==1)
		{
			//check new and confirm
			if($newPassword === $confirmPassword)
			{
				//change password
				$this->subAdminModel->changePassword($_SESSION['subAdminName'],$newPassword);
				$data['success']=true;
				$data['success_message']='Password changed successfully';
				$this->load->view('status_message',$data);
			}
			else
			{
				$data['success']=false;
				$data['fail_message']='Password and Confirm Password not matched. Please Try Again.';
				$this->load->view('status_message',$data);
			}
		}
		else
		{
			$data['success']=false;
			$data['fail_message']='Wrong Password. Please Try Again Later.';
			$this->load->view('status_message',$data);
		}
		
		
		
		//echo $oldPassword;
	}
}