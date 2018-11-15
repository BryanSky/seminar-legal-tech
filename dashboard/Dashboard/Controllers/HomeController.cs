using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;
using Dashboard.Models;

namespace Dashboard.Controllers {
    public class HomeController : Controller {
        public ActionResult Index() {
            var vm = new StartupViewModel();

            return View(vm);
        }

        public ActionResult About() {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact() {
            ViewBag.Message = "Your contact page.";

            return View();
        }
    }
}