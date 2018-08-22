using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using WPFDatingApp.ApiClasses;
using WPFDatingApp.Helpers;
using WPFDatingApp.Login;
using Page = System.Windows.Controls.Page;

namespace WPFDatingApp
{
    /// <summary>
    /// Interaction logic for Swiper.xaml
    /// </summary>
    public partial class Swiper : Page
    {
        private const string NameString = "Name: ";
        private const string AgeString = "Age: ";
        public Matches Matches { get; set; }

        public Swiper()
        {
            InitializeComponent();
            GetMatches();
            UpdateView();
        }

        private void UpdateView()
        {
            if (Matches.Page.Rows == null)
            {
                // Have no new matches
                return;
            }
            var current = Matches.Page.Rows.Row.FirstOrDefault();
            if (current == null)
            {
                return;
            }

            otherProfileImage.Source = new BitmapImage(new Uri(current.ProfilePicture));
            lblName.Content = NameString + current.FirstName + " " + current.LastName;
            lblAge.Content = AgeString + current.Age;
        }

        private void GetMatches()
        {
            //GetUsers
            var matches = HttpClientHelper.Get<Matches>("GetMatches");

            if (matches == null)
            {
                return;
            }

            Matches = matches;
        }

        private void SendLike(int like, long userId)
        {
            var matchesList = Matches.Page.Rows.Row;

            if (matchesList.Count <= 0)
            {
                return;
            }

            // Do something with like
            HttpClientHelper.Get<JsonLogin>($"AjourLike?USER_ID={userId}&LIKE={like}");

            // Remove user from matches list
            Matches.Page.Rows.Row.Remove(matchesList.First());

            // Update view with new match
            UpdateView();
        }

        private void btnLike_Click(object sender, RoutedEventArgs e)
        {

            var matchesList = Matches.Page?.Rows?.Row;

            if (matchesList == null || matchesList.Count <= 0)
            {
                return;
            }

            SendLike(1, matchesList.FirstOrDefault().UserId);
        }

        private void btnDislike_Click(object sender, RoutedEventArgs e)
        {
            var matchesList = Matches.Page?.Rows?.Row;

            if (matchesList == null || matchesList.Count <= 0)
            {
                return;
            }

            SendLike(0, matchesList.FirstOrDefault().UserId);
        }
    }
}
