<?php

/**
*	Controller for all admin actions
*/

defined('BASEPATH') OR exit('No direct script access allowed');

class Admin extends CI_Controller {
	  
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
		 
		/**
			If not logged in, redirect to login page
		*/
		if(!isset($_SESSION["admin_name"]))
		{
			redirect('/home', 'refresh');
		}
		  
		//load models
		$this->load->model('admin_model');
		$this->load->model('log_model');
		$this->load->model('post_model');
		
		$this->load->view('templates/header2');
		
		require_once("Constants.php");
     }
	 
	
	
	/**
	*	[ADMIN HOME PAGE] --- Search is the default activity
	*/ 
	public function index()			
	{
		redirect('admin/search','refresh');
	}
	
	/**
		Nothing important ; just for test purpose
	*/
	public function insert_log_test()
	{
		$data = array();
		
		$data['user_id']=1;
		$data['cat_id']=2;
		$data['time']='2015-12-06 02:00:00';
		$data['log_type']=3;
		$data['post_id']=2;
		$data['prev_status']=3;
		$data['changed_status']=1;
		$data['cat_name']="Test";
		
		$this->log_model->insert_log($data);
	}
	
	/**
		test purpose -- unnecessary here
	*/
	public function distance($lat1, $lon1, $lat2, $lon2) {

	  $theta = $lon1 - $lon2;
	  $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
	  $dist = acos($dist);
	  $dist = rad2deg($dist);
	  $miles = $dist * 60 * 1.1515;
	  
	  return $miles * 1609.344;
	}

	/**
		unnecessary. just for testing distance function
	*/
	public function test3()
	{
		echo '<br><br><br>';
		echo $this->distance(32.9697, -96.80322, 29.46786, -98.53506, "M") . " Meters<br>";
	}
	
	/**
		Search Reported Posts
		Based on Category , Location , Status (0->PENDING ; 1->VERIFIED ; 2-> REJECTED ; 3-> SOLVED) and time
	*/
	public function search()
	{
		if(!isset($_POST['location_select']))
		{
			$data['is_set']=false;
			
			//if empty -> no problem
			$data['n_loc']=$this->admin_model->get_all_locations();
			
			//if empty -> no problem
			$data['catData'] = $this->post_model->get_all_categories();
			
			$search_key=array();
			$search_key['location']="ANY";
			$search_key['category']="ANY";
			$search_key['duration']="ANY";
			$search_key['status']="ANY";
			$search_key['filter'] = 1;
			
			$data['search_key'] = $search_key;
			
			$this->load->view('adminhome',$data);
		}
		else
		{
			$data['n_loc']=$this->admin_model->get_all_locations();
			$data['catData'] = $this->post_model->get_all_categories();
			
			$search_key=array();
			
			if(isset($_POST['location_select'])) $search_key['location']=$_POST['location_select'];
			else $search_key['location']="ANY";
			
			if(isset($_POST['category_select'])) $search_key['category']=$_POST['category_select'];
			else $search_key['category']="ANY";
			
			if(isset($_POST['duration_select'])) $search_key['duration']=$_POST['duration_select'];
			else $search_key['duration']="ANY";
			
			if(isset($_POST['status_select'])) $search_key['status']=$_POST['status_select'];
			else $search_key['status']="ANY";
			
			if(!empty($_POST['check'])) $filtered_search = FILTERED_SEARCH_OFF;
			else $filtered_search = FILTERED_SEARCH_ON;
			
			$search_key['filter'] = $filtered_search;
			
			//load last 20 posts using the search_key combination
			$data['is_set']=true;
			
			if($filtered_search == FILTERED_SEARCH_ON)
			{
				$posts=$this->admin_model->search_post($search_key);
			}
			else
			{
				$posts=$this->admin_model->search_all_post($search_key);
			}
			
			$data['posts']=array();
			foreach($posts as $p)
			{
				$post=$p;
				$temp=$this->post_model->get_vote_count($p['post_id']);
				
				$post['up_votes']=$temp['upvotes'];
				$post['down_votes']=$temp['downvotes'];
				
				$post['user_name']=$this->post_model->get_user_name($p['user_id']);
				
				$post['user_rating']=$this->post_model->get_current_rating($p['user_id']);
				
				$post['cat_name'] = $this->post_model->get_category_name($p['category']);
				
				$post['location']=$this->post_model->get_location($p['actual_location_id']);
				
				array_push($data['posts'],$post);
			}
			$data['search_key'] = $search_key;
			$this->load->view('adminhome',$data);
		}
	}
	
	
	/**
		Change the status of a post (verify, reject or solve)
	*/
	public function take_action($post_id)
	{
		if(isset($_POST['action'])) $action = $_POST['action'];
		else redirect('admin/search','refresh');
		
		//CHANGE STATUS OF THE POST
		$this->post_model->update_post_status($post_id,$action);
		
		//CALCULATE RATING CHANGE
		$change=0;
		if($action==STATUS_PENDING)
		{
			//DO NOTHING
		}
		else if($action==STATUS_VERIFIED)
		{
			//Exponential Function Later
			$change= STATUS_VERIFY_POINT;
		}
		else if($action==STATUS_REJECTED)
		{
			$change= STATUS_REJECT_POINT;
		}
		else if($action==STATUS_SOLVED)
		{
			//DO NOTHING
		}
		
		$this->post_model->update_rating_change($post_id,$change);
		
		$data['success']=true;
		$data['success_message']="Status and user vote count updated successfully";

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		
		$logData['cat_id']=$this->post_model->getCategoryId($post_id);
		
		$logData['log_type']=LOG_ADMIN_ACTION;
		$logData['post_id']=$post_id;
		$logData['changed_status']=$action;
		$logData['cat_name']=$this->post_model->get_category_name($logData['cat_id']);
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}
	
	/**
	*	Admin Logs out
	*/
	
	public function logout()	
	{
		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=-1;
		$logData['log_type']=LOG_ADMIN_LOGOUT;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']='';
				
		$this->log_model->insert_log($logData);

		unset($_SESSION["admin_id"]);
		unset($_SESSION["admin_name"]);
		
		//$this->session->sess_destroy();	//!Stop Session 
		
		/**
		*Redirect To Homepage
		*/
		
		redirect('/home', 'refresh');
	}
	
	/**
		Show problem counts in a brachart
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
			$newCat[0] = "unused";
			$newCat[1] = $this->post_model->get_category_name($id);
			
			$count = $this->post_model->category_problem_count($id);
			
			$sCount = $this->post_model->category_solved_count($id);
			
			$cats[$i]=$newCat;
			$pCount[$i] = $count;
			$solvedCount[$i] = $sCount;
			
			$i++;
		}
		
		
		$data['pCounts'] = $pCount;
		$data['cats'] = $cats;
		$data['solvedCounts'] = $solvedCount;
		
		$locations = array();	
		$lCounts = array();
		$solvedCounts2 = array();


		$nbrhood = $this->post_model->get_all_neighborhoods();
		foreach ($nbrhood as $n) {
			$element = array();
			$element['loc_id']=$this->post_model->get_nbrhd_location_ids($n['neighbourhood']);
			$element['problem_count'] = $this->post_model->get_problem_count_location($element['loc_id']);
			$element['solved_count'] = $this->post_model->get_solved_count_location($element['loc_id']);
			
			array_push($locations,$n['neighbourhood']);				
			array_push($lCounts, $element['problem_count']);	
			array_push($solvedCounts2, $element['solved_count']);

		}

		$data['lCounts'] = $lCounts;
		$data['locations'] = $locations;
		$data['solvedCounts2'] = $solvedCounts2;
		
		$this->load->view("showBarGraph", $data);
		
	}

	/**
		Generate UI data for add category
	*/
	public function addCategory()
	{
		$catData = $this->post_model->get_suggested_categories();

		$data['catData'] = $catData;
		$data['existingCat'] = $this->post_model->get_all_categories();
		
		unset($data['existingCat'][0]);
		
		$this->load->view('addCategory',$data);
	}
	
	/**
		Take action for add cateogory
	*/
	public function addCategoryAction($id)
	{
		//get category name from suggestedCategories table
		$cat_name = $this->post_model->get_suggested_cat_name($id);
		
		//remove it from suggestedCategories table
		$this->post_model->delete_suggested_cat($id);
		
		//insert it into categories table
		$this->post_model->insert_cat($cat_name);
		
		$data['success']=true;
		$data['success_message']='Category added successfully.';

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=$this->post_model->get_category_id_from_name($cat_name);
		$logData['log_type']=LOG_ADD_CATEGORY;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']=$cat_name;
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}
	
	/**
		Add Category -> submit button action
	*/
	public function addNewCategory()
	{
		$cat_name = $_POST['newCat'];
		//insert it into categories table
		$this->post_model->insert_cat($cat_name);
		
		$data['success']=true;
		$data['success_message']='Category added successfully.';

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=$this->post_model->get_category_id_from_name($cat_name);
		$logData['log_type']=LOG_ADD_CATEGORY;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']=$cat_name;
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}
	
	/**
		Generate UI
	*/
	public function deleteCategory()
	{
		$data['existingCat'] = $this->post_model->get_all_categories();
		unset($data['existingCat'][0]);
		$this->load->view('deleteCategory',$data);
	}
	
	/**
		Take action to delete cateogory
	*/
	public function deleteCategoryAction($id)
	{
		$cat_name = $this->post_model->get_category_name($id);
		$this->post_model->delete_cat($id);
		
		$data['success']=true;
		$data['success_message']='Category deleted successfully.';

		$logData = array();
		
		$logData['user_id']=$_SESSION["admin_id"];
		$logData['cat_id']=-1;
		$logData['log_type']=LOG_DELETE_CATEGORY;
		$logData['post_id']=-1;
		$logData['changed_status']=-1;
		$logData['cat_name']=$cat_name;
		
		$this->log_model->insert_log($logData);

		$this->load->view('status_message',$data);
	}

	/**
		Show problems in a map
	*/
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
		
		$data['mapData'] = $existingCat;
		
		$this->load->view('showMap',$data);
	}
	
	/**
		Show a reported problem (post) in map
		param : $id -> post id
	*/
	public function showALocation($id)
	{
		$data['location'] = $this->post_model->get_post_location($id);
		
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
		if($this->admin_model->checkPassword($_SESSION['admin_name'],$oldPassword)==1)
		{
			//check new and confirm
			if($newPassword === $confirmPassword)
			{
				//change password
				$this->admin_model->changePassword($_SESSION['admin_name'],$newPassword);
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
	
	public function addSubAdmin()
	{
		//echo '<br><br><br>';
		$data['subAdminList'] = $this->admin_model->getSubAdminList();
		
		//generate list value:id, text: name
		$this->load->view('addSubAdmin',$data);
	}
	
	public function addSubAdminAction()
	{
		$name = $_POST['name'];
		$password = md5($_POST['password']);
		
		$this->admin_model->addSubAdmin($name,$password);
		
		$data['success']=true;
		$data['success_message']='Sub-Admin added successfully';
		$this->load->view('status_message',$data);
	}
	
	public function deleteSubAdminAction($id)
	{
		$this->admin_model->deleteSubAdmin($id);
		$data['success']=true;
		$data['success_message']='Sub-Admin deleted successfully';
		$this->load->view('status_message',$data);
	}
}